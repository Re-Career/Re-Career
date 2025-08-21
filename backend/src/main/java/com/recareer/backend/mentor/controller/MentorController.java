package com.recareer.backend.mentor.controller;

import com.recareer.backend.availableTime.dto.AvailableTimeResponseDto;
import com.recareer.backend.availableTime.dto.AvailableTimeRequestDto;
import com.recareer.backend.availableTime.entity.AvailableTime;
import com.recareer.backend.feedback.dto.MentorFeedbackListResponseDto;
import com.recareer.backend.mentor.dto.MentorCreateRequestDto;
import com.recareer.backend.mentor.dto.MentorCreateResponseDto;
import com.recareer.backend.mentor.dto.MentorDetailResponseDto;
import com.recareer.backend.mentor.dto.MentorSummaryResponseDto;
import com.recareer.backend.mentor.dto.MentorUpdateRequestDto;
import com.recareer.backend.mentor.dto.MentorUpdateResponseDto;
import com.recareer.backend.mentor.dto.MentorSearchRequestDto;
import com.recareer.backend.mentor.dto.MentorFiltersResponseDto;
import com.recareer.backend.mentor.dto.MentorSearchResponse;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.service.MentorService;
import com.recareer.backend.auth.service.JwtTokenProvider;
import com.recareer.backend.session.dto.SessionListResponseDto;
import com.recareer.backend.session.entity.Session;
import com.recareer.backend.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.recareer.backend.auth.util.AuthUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mentors")
@RequiredArgsConstructor
@Tag(name = "Mentor", description = "멘토 관련 API")
public class MentorController {

    private final MentorService mentorService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthUtil authUtil;

    @PostMapping
    @Operation(summary = "멘토 등록", description = "새로운 멘토를 등록합니다")
    public ResponseEntity<ApiResponse<MentorCreateResponseDto>> createMentor(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody MentorCreateRequestDto requestDto) {
        try {
            Long userId = authUtil.validateTokenAndGetUserId(accessToken);
            
            // 요청 DTO의 userId와 토큰의 userId가 일치하는지 확인
            if (!userId.equals(requestDto.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("본인의 이름으로만 멘토를 등록할 수 있습니다"));
            }
            
            Mentor mentor = mentorService.createMentor(requestDto);
            MentorCreateResponseDto responseDto = MentorCreateResponseDto.from(mentor);
            return ResponseEntity.ok(ApiResponse.success("멘토가 성공적으로 등록되었습니다.", responseDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Mentor creation failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponse.error("멘토 등록에 실패했습니다."));
        }
    }

    @GetMapping
    @Operation(summary = "홈 - 당신을 위한 멘토들", description = "유저가 속한 지역의 멘토 리스트를 조회합니다")
    public ResponseEntity<ApiResponse<List<MentorSummaryResponseDto>>> getMentorsByProvince(
            @RequestParam(required = false, defaultValue = "1") Long provinceId) {
        try {
            List<MentorSummaryResponseDto> mentors = mentorService.getMentorsByProvince(provinceId);
            return ResponseEntity.ok(ApiResponse.success(mentors));
        } catch (Exception e) {
            log.error("Get mentors by province failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponse.error("멘토 목록 조회에 실패했습니다."));
        }
    }

    @GetMapping("/search")
    @Operation(summary = "멘토 검색", description = "키워드, 직업, 경력, 지역, 성향으로 멘토를 검색합니다. Primary(1순위: 지역/성향, 2순위: 직업/경험)와 Secondary(직업/경험만) 결과를 동시에 반환합니다.")
    public ResponseEntity<ApiResponse<MentorSearchResponse>> searchMentors(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<Long> positionIds,
            @RequestParam(required = false) List<String> experiences,
            @RequestParam(required = false) List<Long> provinceIds,
            @RequestParam(required = false) List<Long> personalityTagIds) {
        
        try {
            MentorSearchRequestDto searchRequest = new MentorSearchRequestDto(
                keyword, positionIds, experiences, provinceIds, personalityTagIds
            );
            MentorSearchResponse mentors = mentorService.searchMentorsWithPrimarySecondary(searchRequest);
            return ResponseEntity.ok(ApiResponse.success(mentors));
            
        } catch (Exception e) {
            log.error("Search mentors failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponse.error("멘토 검색에 실패했습니다."));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "멘토 프로필 조회", description = "멘토의 프로필을 조회합니다")
    public ResponseEntity<ApiResponse<MentorDetailResponseDto>> getMentorById(@PathVariable Long id) {
        try {
            return mentorService.getMentorDetailById(id)
                    .map(dto -> ResponseEntity.ok(ApiResponse.success(dto)))
                    .orElse(ResponseEntity.status(404).body(ApiResponse.error("해당 멘토를 찾을 수 없습니다.")));
        } catch (Exception e) {
            log.error("Error getting mentor by id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body(ApiResponse.error("멘토 정보 조회 중 오류가 발생했습니다."));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "멘토 프로필 수정", description = "멘토의 프로필 정보를 수정합니다")
    public ResponseEntity<ApiResponse<MentorUpdateResponseDto>> updateMentor(
            @PathVariable Long id,
            @RequestHeader("Authorization") String accessToken,
            @RequestBody MentorUpdateRequestDto requestDto) {
        Long userId = authUtil.validateTokenAndGetUserId(accessToken);
        if (!userId.equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 작업에 대한 권한이 없습니다.");
        }
        return mentorService.updateMentor(id, requestDto.getPositionId(), requestDto.getDescription(), requestDto.getIntroduction(), requestDto.getExperience(), requestDto.getSkillIds())
                .map(mentor -> ResponseEntity.ok(ApiResponse.success("멘토 정보가 성공적으로 수정되었습니다.", MentorUpdateResponseDto.from(mentor))))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("해당 멘토를 찾을 수 없습니다.")));
    }

    // @GetMapping("/{id}/sessions")
    // @Operation(summary = "멘토별 세션 목록 조회", description = "멘토별 예정된 상담을 조회합니다 - /my/sessions?role=MENTOR 사용 권장")
    // public ResponseEntity<ApiResponse<List<SessionListResponseDto>>> getMentorSessions(@PathVariable Long id, @RequestHeader("Authorization") String accessToken) {
    //     Long userId = authUtil.validateTokenAndGetUserId(accessToken);
    //     if (!userId.equals(id)) {
    //         throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 작업에 대한 권한이 없습니다.");
    //     }
    //     List<Session> sessions = mentorService.getMentorSessions(id);
    //     List<SessionListResponseDto> sessionDtos = sessions.stream()
    //             .map(SessionListResponseDto::from)
    //             .toList();

    //     return ResponseEntity.ok(ApiResponse.success(sessionDtos));
    // }

    @GetMapping("/{id}/available-times")
    @Operation(summary = "멘토 상담 예약 - 가능한 날짜, 시간 조회", description = "각 멘토 별 멘토링이 가능한 시간대를 조회합니다")
    public ResponseEntity<ApiResponse<List<AvailableTimeResponseDto>>> getMentorAvailableTimes(@PathVariable Long id) {
        List<AvailableTime> availableTimes = mentorService.getMentorAvailableTimes(id);
        List<AvailableTimeResponseDto> availableTimeDtos = availableTimes.stream()
                .map(AvailableTimeResponseDto::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(availableTimeDtos));
    }

    @PostMapping("/{id}/available-times")
    @Operation(summary = "멘토링 가능한 시간대 생성", description = "각 멘토 별 멘토링이 가능한 시간대를 추가합니다")
    public ResponseEntity<ApiResponse<AvailableTimeResponseDto>> createMentorAvailableTime(
            @PathVariable Long id,
            @RequestHeader("Authorization") String accessToken,
            @Valid @RequestBody AvailableTimeRequestDto requestDto) {
        Long userId = authUtil.validateTokenAndGetUserId(accessToken);
        if (!userId.equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 작업에 대한 권한이 없습니다.");
        }
        try {
            AvailableTime newAvailableTime = mentorService.createMentorAvailableTime(id, requestDto.getAvailableTime());

            return ResponseEntity.ok(ApiResponse.success("멘토링 가능 시간이 성공적으로 추가되었습니다.", AvailableTimeResponseDto.from(newAvailableTime)));
        } catch (IllegalArgumentException e) {
            log.error("Create mentor available time failed: {}", e.getMessage());

            return ResponseEntity.badRequest().body(ApiResponse.error("멘토를 찾을 수 없거나 잘못된 시간 형식입니다."));
        } catch (Exception e) {
            log.error("Create mentor available time failed: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(ApiResponse.error("멘토링 가능 시간 추가에 실패했습니다."));
        }
    }

    @GetMapping("/{id}/feedbacks")
    @Operation(summary = "멘토 피드백 조회", description = "특정 멘토에 대한 피드백 리스트와 전체 개수를 조회합니다")
    public ResponseEntity<ApiResponse<MentorFeedbackListResponseDto>> getMentorFeedbacks(@PathVariable Long id) {
        try {
            MentorFeedbackListResponseDto responseDto = mentorService.getMentorFeedbacks(id);
            return ResponseEntity.ok(ApiResponse.success(responseDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Get mentor feedbacks failed for mentorId {}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("멘토 피드백 조회에 실패했습니다."));
        }
    }

    @GetMapping("/filters")
    @Operation(summary = "멘토 필터 옵션 조회", description = "멘토 검색에 사용하는 직업, 지역, 성향 필터 옵션들을 조회합니다")
    public ResponseEntity<ApiResponse<MentorFiltersResponseDto>> getFilters() {
        try {
            MentorFiltersResponseDto filters = mentorService.getFilters();
            return ResponseEntity.ok(ApiResponse.success(filters));
        } catch (Exception e) {
            log.error("Get filters failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponse.error("필터 옵션 조회에 실패했습니다."));
        }
    }
}

//IS 40만원 - 2만 + 320만 = 358만
//