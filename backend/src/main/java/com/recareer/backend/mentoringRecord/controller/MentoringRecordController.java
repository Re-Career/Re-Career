package com.recareer.backend.mentoringRecord.controller;

import com.recareer.backend.mentoringRecord.dto.MentoringRecordRequestDto;
import com.recareer.backend.mentoringRecord.dto.MentoringRecordResponseDto;
import com.recareer.backend.mentoringRecord.entity.MentoringRecord;
import com.recareer.backend.mentoringRecord.service.MentoringRecordService;
import com.recareer.backend.response.ApiResponse;
import com.recareer.backend.auth.util.AuthUtil;
import com.recareer.backend.reservation.entity.Reservation;
import com.recareer.backend.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/mentoring_records")
@RequiredArgsConstructor
@Tag(name = "MentoringRecord", description = "멘토링 기록 관련 API")
public class MentoringRecordController {

    private final MentoringRecordService mentoringRecordService;
    private final AuthUtil authUtil;
    private final ReservationService reservationService;

    @GetMapping("/users/{userId}")
    @Operation(summary = "완료된 상담 리스트 조회", description = "특정 유저의 완료된 멘토링 기록 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<MentoringRecordResponseDto>>> getCompletedMentoringRecordsByUserId(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable Long userId) {
        
        try {
            Long requestUserId = authUtil.validateTokenAndGetUserId(accessToken);
            
            // 자신의 완료된 상담 기록만 조회 가능
            if (!requestUserId.equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("본인의 완료된 상담 기록만 조회할 수 있습니다"));
            }
            
            List<MentoringRecordResponseDto> mentoringRecords = mentoringRecordService.findCompletedMentoringRecordsByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(mentoringRecords));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "완료된 상담 상세 조회")
    public ResponseEntity<ApiResponse<MentoringRecordResponseDto>> getMentoringRecord(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable Long id) {
        
        try {
            Long userId = authUtil.validateTokenAndGetUserId(accessToken);
            MentoringRecord mentoringRecord = mentoringRecordService.findMentoringRecordById(id);
            
            // 해당 멘토링의 참여자만 조회 가능
            if (!mentoringRecord.getReservation().isMentorParticipant(userId) && 
                !mentoringRecord.getReservation().isMenteeParticipant(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("해당 멘토링 참여자만 조회할 수 있습니다"));
            }
            
            MentoringRecordResponseDto responseDto = MentoringRecordResponseDto.from(mentoringRecord);

            return ResponseEntity.ok(ApiResponse.success(responseDto));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{reservationId}/mento-feedback")
    @Operation(summary = "멘토 피드백 작성", description = "멘티가 멘토링 후 멘토에 대한 피드백을 작성합니다.")
    public ResponseEntity<ApiResponse<Long>> addMenteeFeedback(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable Long reservationId,
            @Valid @RequestBody MentoringRecordRequestDto requestDto) {
        
        try {
            Long userId = authUtil.validateTokenAndGetUserId(accessToken);
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
            Long userId = authUtil.validateTokenAndGetUserId(accessToken);
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