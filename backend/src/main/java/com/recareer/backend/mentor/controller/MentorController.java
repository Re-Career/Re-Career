package com.recareer.backend.mentor.controller;

import com.recareer.backend.availableTime.dto.AvailableTimeResponseDto;
import com.recareer.backend.availableTime.entity.AvailableTime;
import com.recareer.backend.mentor.dto.MentorCreateRequestDto;
import com.recareer.backend.mentor.dto.MentorCreateResponseDto;
import com.recareer.backend.mentor.dto.MentorDetailResponseDto;
import com.recareer.backend.mentor.dto.MentorFilterRequestDto;
import com.recareer.backend.mentor.dto.MentorListResponseDto;
import com.recareer.backend.mentor.dto.MentorSummaryResponseDto;
import com.recareer.backend.mentor.dto.MentorUpdateResponseDto;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.entity.MentoringType;
import com.recareer.backend.mentor.service.MentorService;
import com.recareer.backend.auth.service.JwtTokenProvider;
import com.recareer.backend.reservation.dto.ReservationListResponseDto;
import com.recareer.backend.reservation.entity.Reservation;
import com.recareer.backend.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    @Operation(summary = "멘토 등록", description = "새로운 멘토를 등록합니다")
    public ResponseEntity<ApiResponse<MentorCreateResponseDto>> createMentor(
            @RequestBody MentorCreateRequestDto requestDto) {
        try {
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
    public ResponseEntity<ApiResponse<List<MentorSummaryResponseDto>>> getMentorsByRegion(
            @RequestParam(required = false, defaultValue = "서울") String region) {
        try {
            List<MentorSummaryResponseDto> mentors = mentorService.getMentorsByRegion(region);
            return ResponseEntity.ok(ApiResponse.success(mentors));
        } catch (Exception e) {
            log.error("Get mentors by region failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponse.error("멘토 목록 조회에 실패했습니다."));
        }
    }


    @GetMapping("/search/recommended")
    @Operation(summary = "멘토 찾기 - 추천 매칭", description = "필터링 우선순위대로 멘토를 추천합니다. 1순위: 지역/성향, 2순위: 직업/경험/미팅방식")
    public ResponseEntity<ApiResponse<List<MentorListResponseDto>>> getSearchRecommendedMentors(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam(required = false) List<String> regions,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String experience,
            @RequestParam(required = false) MentoringType mentoringType) {
        
        try {
            String token = accessToken.replace("Bearer ", "");
            
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401)
                        .body(ApiResponse.error("유효하지 않은 토큰입니다."));
            }

            String providerId = jwtTokenProvider.getProviderIdFromToken(token);
            
            // 필터링 우선순위대로 멘토 추천
            List<Mentor> mentors = mentorService.getMentorsByPriorityFilters(
                providerId, regions, position, experience, mentoringType);
            
            List<MentorListResponseDto> mentorDtos = mentors.stream()
                    .map(MentorListResponseDto::from)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success(mentorDtos));
            
        } catch (Exception e) {
            log.error("Get search recommended mentors failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponse.error("추천 매칭 조회에 실패했습니다."));
        }
    }

    @PostMapping("/search")
    @Operation(summary = "멘토 찾기 - 결과 조회", description = "직업/경험/미팅방식으로 필터링한 멘토를 조회합니다.")
    public ResponseEntity<ApiResponse<List<MentorSummaryResponseDto>>> searchMentorsByFilters(
            @RequestBody MentorFilterRequestDto filterRequest) {
        
        try {
            List<MentorSummaryResponseDto> mentors = mentorService.getMentorsByFilters(filterRequest);
            return ResponseEntity.ok(ApiResponse.success(mentors));
            
        } catch (Exception e) {
            log.error("Search mentors by filters failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponse.error("멘토 검색에 실패했습니다."));
        }
    }

    @GetMapping("/search/list")
    @Operation(summary = "멘토 찾기 - 리스트 조회", description = "모든 멘토를 조회합니다.")
    public ResponseEntity<ApiResponse<List<MentorSummaryResponseDto>>> getMentorsList() {
        
        try {
            // 빈 필터로 모든 멘토 조회
            MentorFilterRequestDto emptyFilter = MentorFilterRequestDto.builder().build();
            List<MentorSummaryResponseDto> mentors = mentorService.getMentorsByFilters(emptyFilter);
            return ResponseEntity.ok(ApiResponse.success(mentors));
            
        } catch (Exception e) {
            log.error("Get mentors list failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponse.error("멘토 목록 조회에 실패했습니다."));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "멘토 프로필 조회", description = "멘토의 프로필을 조회합니다")
    public ResponseEntity<ApiResponse<MentorDetailResponseDto>> getMentorById(@PathVariable Long id) {
        return mentorService.getMentorDetailById(id)
                .map(dto -> ResponseEntity.ok(ApiResponse.success(dto)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "멘토 프로필 수정", description = "멘토의 프로필 정보를 수정합니다")
    public ResponseEntity<ApiResponse<MentorUpdateResponseDto>> updateMentor(
            @PathVariable Long id,
            @RequestParam String position,
            @RequestParam String description,
            @RequestParam(required = false) String introduction,
            @RequestParam(required = false) List<String> skills) {
        return mentorService.updateMentor(id, position, description, introduction, skills)
                .map(mentor -> ResponseEntity.ok(ApiResponse.success("멘토 정보가 성공적으로 수정되었습니다.", MentorUpdateResponseDto.from(mentor))))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("해당 멘토를 찾을 수 없습니다.")));
    }

    @GetMapping("/{id}/reservations")
    @Operation(summary = "멘토별 예약 목록 조회", description = "멘토별 예정된 상담을 조회합니다")
    public ResponseEntity<ApiResponse<List<ReservationListResponseDto>>> getMentorReservations(@PathVariable Long id) {
        List<Reservation> reservations = mentorService.getMentorReservations(id);
        List<ReservationListResponseDto> reservationDtos = reservations.stream()
                .map(ReservationListResponseDto::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(reservationDtos));
    }

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
            @RequestParam String availableTime) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(availableTime);
            AvailableTime newAvailableTime = mentorService.createMentorAvailableTime(id, dateTime);

            return ResponseEntity.ok(ApiResponse.success("멘토링 가능 시간이 성공적으로 추가되었습니다.", AvailableTimeResponseDto.from(newAvailableTime)));
        } catch (IllegalArgumentException e) {
            log.error("Create mentor available time failed: {}", e.getMessage());

            return ResponseEntity.badRequest().body(ApiResponse.error("멘토를 찾을 수 없거나 잘못된 시간 형식입니다."));
        } catch (Exception e) {
            log.error("Create mentor available time failed: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(ApiResponse.error("멘토링 가능 시간 추가에 실패했습니다."));
        }
    }
}