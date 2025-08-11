package com.recareer.backend.mentoringRecord.controller;

import com.recareer.backend.mentoringRecord.dto.MentoringRecordRequestDto;
import com.recareer.backend.mentoringRecord.dto.MentoringRecordResponseDto;
import com.recareer.backend.mentoringRecord.entity.MentoringRecord;
import com.recareer.backend.mentoringRecord.service.MentoringRecordService;
import com.recareer.backend.response.ApiResponse;
import com.recareer.backend.auth.service.JwtTokenProvider;
import com.recareer.backend.reservation.entity.Reservation;
import com.recareer.backend.reservation.service.ReservationService;
import com.recareer.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/mentoring_records")
@RequiredArgsConstructor
@Tag(name = "MentoringRecord", description = "멘토링 기록 관련 API")
public class MentoringRecordController {

    private final MentoringRecordService mentoringRecordService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ReservationService reservationService;
    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "완료된 상담 조회")
    public ResponseEntity<ApiResponse<MentoringRecordResponseDto>> getMentoringRecord(@PathVariable Long id) {
        MentoringRecord mentoringRecord = mentoringRecordService.findMentoringRecordById(id);
        MentoringRecordResponseDto responseDto = MentoringRecordResponseDto.from(mentoringRecord);

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    private Long validateTokenAndGetUserId(String accessToken) {
        String token = accessToken.replace("Bearer ", "");
        
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 액세스 토큰입니다");
        }

        String providerId = jwtTokenProvider.getProviderIdFromToken(token);
        return userService.findByProviderId(providerId).getId();
    }

    @PostMapping("/{reservationId}/mento-feedback")
    @Operation(summary = "멘토 피드백 작성", description = "멘티가 멘토링 후 멘토에 대한 피드백을 작성합니다.")
    public ResponseEntity<ApiResponse<Long>> addMenteeFeedback(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable Long reservationId,
            @Valid @RequestBody MentoringRecordRequestDto requestDto) {
        
        try {
            Long userId = validateTokenAndGetUserId(accessToken);
            Reservation reservation = reservationService.findById(reservationId);
            
            if (!reservation.isMenteeParticipant(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("해당 멘토링에 참여한 멘티만 피드백을 작성할 수 있습니다"));
            }
            
            Long mentoringRecordId = mentoringRecordService.createOrUpdateMentoringRecord(reservationId, requestDto);
            return ResponseEntity.ok(ApiResponse.success(mentoringRecordId));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{reservationId}/audio-upload")
    @Operation(summary = "멘토링 녹음 파일 업로드",
               description = "멘토가 멘토링 녹음 파일을 업로드하면 S3에 저장하고, " +
                           "AI를 통해 음성을 텍스트로 전사하고 상담 내용을 요약합니다.")
    public ResponseEntity<ApiResponse<Long>> uploadConsultationAudio(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable Long reservationId,
            @RequestParam("audioFile") MultipartFile audioFile) {
        
        try {
            Long userId = validateTokenAndGetUserId(accessToken);
            Reservation reservation = reservationService.findById(reservationId);
            
            if (!reservation.isMentorParticipant(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("해당 멘토링에 참여한 멘토만 음성 파일을 업로드할 수 있습니다"));
            }
            
            Long mentoringRecordId = mentoringRecordService.uploadAudioAndProcess(reservationId, audioFile);
            return ResponseEntity.ok(ApiResponse.success(mentoringRecordId));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}