package com.recareer.backend.mentoringRecord.service;

import com.recareer.backend.mentoringRecord.dto.MentoringRecordListResponseDto;
import com.recareer.backend.mentoringRecord.dto.MentoringRecordRequestDto;
import com.recareer.backend.mentoringRecord.dto.MentoringRecordResponseDto;
import com.recareer.backend.mentoringRecord.entity.MentoringRecord;
import com.recareer.backend.mentoringRecord.entity.MentoringRecordStatus;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface MentoringRecordService {

    MentoringRecord findMentoringRecordById(Long id);

    Long createOrUpdateMentoringRecord(Long sessionId, MentoringRecordRequestDto requestDto);

    Long uploadAudioAndProcess(Long sessionId, MultipartFile audioFile);
    
    void updateMentoringRecordStatus(Long mentoringRecordId, MentoringRecordStatus status);
    
    MentoringRecord findBySessionId(Long sessionId);
    
    List<MentoringRecordResponseDto> findCompletedMentoringRecordsByUserId(Long userId);
    
    // List<MentoringRecordListResponseDto> findCompletedMentoringRecordsListByUserId(Long userId);
}