package com.recareer.backend.sessionRecord.controller;

import com.recareer.backend.response.ApiResponse;
import com.recareer.backend.sessionRecord.dto.SessionRecordRequestDto;
import com.recareer.backend.sessionRecord.entity.SessionRecord;
import com.recareer.backend.sessionRecord.service.SessionRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/session_records")
@RequiredArgsConstructor
@Tag(name = "SessionRecord", description = " 기록 관련 API")
public class SessionRecordController {

    private final SessionRecordService sessionRecordService;

    @GetMapping("/{id}")
    @Operation(summary = "세션 기록 조회")
    public ResponseEntity<ApiResponse<SessionRecord>> getSessionRecord(@PathVariable Long id) {
        SessionRecord sessionRecord = sessionRecordService.findSessionRecordById(id);

        return ResponseEntity.ok(ApiResponse.success(sessionRecord));
    }

    @PostMapping("/{reservationId}/mentee-feedback")
    @Operation(summary = "멘티 피드백 작성", description = "멘티가 멘토링 후 상담에 대한 피드백을 작성합니다.")
    public ResponseEntity<ApiResponse<Long>> addMenteeFeedback(
            @PathVariable Long reservationId,
            @Valid @RequestBody SessionRecordRequestDto requestDto) {
        Long sessionRecordId = sessionRecordService.createOrUpdateSessionRecord(reservationId, requestDto);

        return ResponseEntity.ok(ApiResponse.success(sessionRecordId));
    }

    @PostMapping("/{reservationId}/audio-upload")
    @Operation(summary = "멘토 상담 녹음 업로드", 
               description = "멘토가 상담 녹음 파일을 업로드하면 S3에 저장하고, " +
                           "AI를 통해 음성을 텍스트로 전사하고 상담 내용을 요약합니다.")
    public ResponseEntity<ApiResponse<Long>> uploadConsultationAudio(
            @PathVariable Long reservationId,
            @RequestParam("audioFile") MultipartFile audioFile) {
        Long sessionRecordId = sessionRecordService.uploadAudioAndProcess(reservationId, audioFile);
        return ResponseEntity.ok(ApiResponse.success(sessionRecordId));
    }
}