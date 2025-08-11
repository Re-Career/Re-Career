package com.recareer.backend.mentoringRecord.service;

import com.recareer.backend.mentoringRecord.dto.MentoringRecordRequestDto;
import com.recareer.backend.mentoringRecord.entity.MentoringRecord;
import com.recareer.backend.mentoringRecord.entity.MentoringRecordStatus;
import org.springframework.web.multipart.MultipartFile;

public interface MentoringRecordService {

    MentoringRecord findMentoringRecordById(Long id);

    Long createOrUpdateMentoringRecord(Long reservationId, MentoringRecordRequestDto requestDto);

    Long uploadAudioAndProcess(Long reservationId, MultipartFile audioFile);
    
    void updateMentoringRecordStatus(Long mentoringRecordId, MentoringRecordStatus status);
    
    MentoringRecord findByReservationId(Long reservationId);
}