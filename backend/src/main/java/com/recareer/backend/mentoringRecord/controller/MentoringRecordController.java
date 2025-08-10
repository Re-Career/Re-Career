package com.recareer.backend.mentoringRecord.controller;

import com.recareer.backend.mentoringRecord.dto.MentoringRecordRequestDto;
import com.recareer.backend.mentoringRecord.entity.MentoringRecord;
import com.recareer.backend.mentoringRecord.service.MentoringRecordService;
import com.recareer.backend.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/mentoring_records")
@RequiredArgsConstructor
@Tag(name = "MentoringRecord", description = "멘토링 기록 관련 API")
public class MentoringRecordController {

    private final MentoringRecordService mentoringRecordService;

    @GetMapping("/{id}")
    @Operation(summary = "멘토링 기록 조회")
    public ResponseEntity<ApiResponse<MentoringRecord>> getMentoringRecord(@PathVariable Long id) {
        MentoringRecord mentoringRecord = mentoringRecordService.findMentoringRecordById(id);

        return ResponseEntity.ok(ApiResponse.success(mentoringRecord));
    }

    @PostMapping("/{reservationId}/mentee-feedback")
    @Operation(summary = "멘티 피드백 작성", description = "멘티가 멘토링 후 상담에 대한 피드백을 작성합니다.")
    public ResponseEntity<ApiResponse<Long>> addMenteeFeedback(
            @PathVariable Long reservationId,
            @Valid @RequestBody MentoringRecordRequestDto requestDto) {
        Long mentoringRecordId = mentoringRecordService.createOrUpdateMentoringRecord(reservationId, requestDto);

        return ResponseEntity.ok(ApiResponse.success(mentoringRecordId));
    }

    @PostMapping("/{reservationId}/audio-upload")
    @Operation(summary = "멘토 상담 녹음 업로드", 
               description = "멘토가 상담 녹음 파일을 업로드하면 S3에 저장하고, " +
                           "AI를 통해 음성을 텍스트로 전사하고 상담 내용을 요약합니다.")
    public ResponseEntity<ApiResponse<Long>> uploadConsultationAudio(
            @PathVariable Long reservationId,
            @RequestParam("audioFile") MultipartFile audioFile) {
        Long mentoringRecordId = mentoringRecordService.uploadAudioAndProcess(reservationId, audioFile);
        return ResponseEntity.ok(ApiResponse.success(mentoringRecordId));
    }
}