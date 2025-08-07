package com.recareer.backend.mentor.controller;

import com.recareer.backend.availableTime.entity.AvailableTime;
import com.recareer.backend.mentor.dto.MentorListResponseDto;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.service.MentorService;
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

    @GetMapping("/region")
    @Operation(summary = "지역, 성향별 인증된 멘토 조회", description = "사용자와 동일한 지역에 거주하며, 같은 성향을 가진 멘토 리스트를 조회합니다.")
    public ResponseEntity<ApiResponse<List<MentorListResponseDto>>> getMentorsByRegion(
            @RequestParam(required = false, defaultValue = "서울시") String region,
            @RequestParam(required = false) List<Long> personalityTags) {
        
        try {
            List<Mentor> mentors = mentorService.getMentorsByRegionAndPersonalityTags(region, personalityTags);
            List<MentorListResponseDto> mentorDtos = mentors.stream()
                    .map(MentorListResponseDto::from)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success(mentorDtos));
            
        } catch (Exception e) {
            log.error("Get mentors by region failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponse.error("해당 지역별 멘토 조회에 실패했습니다."));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "ID로 인증된 멘토 조회", description = "멘토를 검색합니다")
    public ResponseEntity<ApiResponse<MentorListResponseDto>> getMentorById(@PathVariable Long id) {
        return mentorService.getVerifiedMentorById(id)
                .map(mentor -> ResponseEntity.ok(ApiResponse.success(MentorListResponseDto.from(mentor))))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "멘토 정보 수정", description = "멘토의 마이 페이지에서 개인정보를 수정합니다 ")
    public ResponseEntity<ApiResponse<Mentor>> updateMentor(
            @PathVariable Long id,
            @RequestParam String position,
            @RequestParam String description) {
        return mentorService.updateMentor(id, position, description)
                .map(mentor -> ResponseEntity.ok(ApiResponse.success("멘토 정보가 성공적으로 수정되었습니다.", mentor)))
                .orElse(ResponseEntity.status(404).body(ApiResponse.error("해당 멘토를 찾을 수 없습니다.")));
    }

    @GetMapping("/{id}/reservations")
    @Operation(summary = "멘토별 예약 목록 조회", description = "멘토별 예정된 상담을 조회합니다")
    public ResponseEntity<ApiResponse<List<Reservation>>> getMentorReservations(@PathVariable Long id) {
        List<Reservation> reservations = mentorService.getMentorReservations(id);

        return ResponseEntity.ok(ApiResponse.success(reservations));
    }

    @GetMapping("/{id}/available-times")
    @Operation(summary = "멘토별 가능 시간 조회", description = "각 멘토 별 멘토링이 가능한 시간대를 조회합니다")
    public ResponseEntity<ApiResponse<List<AvailableTime>>> getMentorAvailableTimes(@PathVariable Long id) {
        List<AvailableTime> availableTimes = mentorService.getMentorAvailableTimes(id);

        return ResponseEntity.ok(ApiResponse.success(availableTimes));
    }

    @PostMapping("/{id}/available-times")
    @Operation(summary = "멘토 가능 시간 생성", description = "각 멘토 별 멘토링이 가능한 시간대를 추가합니다")
    public ResponseEntity<ApiResponse<AvailableTime>> createMentorAvailableTime(
            @PathVariable Long id,
            @RequestParam String availableTime) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(availableTime);
            AvailableTime newAvailableTime = mentorService.createMentorAvailableTime(id, dateTime);

            return ResponseEntity.ok(ApiResponse.success("멘토 가능 시간이 성공적으로 추가되었습니다.", newAvailableTime));
        } catch (IllegalArgumentException e) {
            log.error("Create mentor available time failed: {}", e.getMessage());

            return ResponseEntity.badRequest().body(ApiResponse.error("멘토를 찾을 수 없거나 잘못된 시간 형식입니다."));
        } catch (Exception e) {
            log.error("Create mentor available time failed: {}", e.getMessage());

            return ResponseEntity.internalServerError().body(ApiResponse.error("멘토 가능 시간 추가에 실패했습니다."));
        }
    }
}