package com.recareer.backend.sessionRecord.service;

import com.recareer.backend.sessionRecord.dto.SessionRecordRequestDto;
import com.recareer.backend.sessionRecord.entity.SessionRecord;
import org.springframework.web.multipart.MultipartFile;

public interface SessionRecordService {

    SessionRecord findSessionRecordById(Long id);

    Long createOrUpdateSessionRecord(Long reservationId, SessionRecordRequestDto requestDto);

    Long uploadAudioAndProcess(Long reservationId, MultipartFile audioFile);
}