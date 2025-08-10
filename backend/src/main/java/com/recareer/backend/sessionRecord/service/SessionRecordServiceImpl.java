package com.recareer.backend.sessionRecord.service;

import com.recareer.backend.common.service.S3Service;
import com.recareer.backend.reservation.entity.Reservation;
import com.recareer.backend.reservation.repository.ReservationRepository;
import com.recareer.backend.sessionRecord.dto.SessionRecordRequestDto;
import com.recareer.backend.sessionRecord.entity.SessionRecord;
import com.recareer.backend.sessionRecord.repository.SessionRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionRecordServiceImpl implements SessionRecordService {

    private final SessionRecordRepository sessionRecordRepository;
    private final ReservationRepository reservationRepository;
    private final S3Service s3Service;
    private final AudioTranscriptionService audioTranscriptionService;

    @Override
    @Transactional(readOnly = true)
    public SessionRecord findSessionRecordById(Long id) {
        return sessionRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 세션 기록을 찾을 수 없습니다."));
    }

    @Override
    @Transactional
    public Long createOrUpdateSessionRecord(Long reservationId, SessionRecordRequestDto requestDto) {
        SessionRecord sessionRecord = findOrCreateSessionRecord(reservationId);
        sessionRecord.setMenteeFeedback(requestDto.getMenteeFeedback());
        
        SessionRecord savedSessionRecord = sessionRecordRepository.save(sessionRecord);
        return savedSessionRecord.getId();
    }

    @Override
    @Transactional
    public Long uploadAudioAndProcess(Long reservationId, MultipartFile audioFile) {
        try {
            log.info("오디오 파일 처리 시작 - 예약 ID: {}, 파일명: {}", reservationId, audioFile.getOriginalFilename());

            // 1. S3에 오디오 파일 업로드
      String audioFileUrl = s3Service.uploadAudioFile(audioFile);
            log.info("S3 업로드 완료: {}", audioFileUrl);

            // 2. 음성을 텍스트로 전사
            String transcribedText = audioTranscriptionService.transcribeAudio(audioFile);
            log.info("음성 전사 완료");

            // 3. 전사된 텍스트 요약
            String summary = audioTranscriptionService.summarizeText(transcribedText);
            log.info("텍스트 요약 완료");

            // 4. 세션 기록 생성 또는 업데이트
            SessionRecord sessionRecord = findOrCreateSessionRecord(reservationId);
            updateSessionRecordWithAudioData(sessionRecord, audioFileUrl, transcribedText, summary);

            SessionRecord savedSessionRecord = sessionRecordRepository.save(sessionRecord);
            log.info("세션 기록 저장 완료 - ID: {}", savedSessionRecord.getId());

            return savedSessionRecord.getId();

        } catch (Exception e) {
            log.error("오디오 파일 처리 중 오류 발생: ", e);
            throw new RuntimeException("오디오 파일 처리에 실패했습니다.", e);
        }
    }

    // 세션 기록을 찾거나 새로 생성하는 공통 로직
    private SessionRecord findOrCreateSessionRecord(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 예약을 찾을 수 없습니다."));

        return sessionRecordRepository.findByReservationId(reservationId)
                .orElse(SessionRecord.builder()
                        .reservation(reservation)
                        .build());
    }

    // 세션 기록에 오디오 관련 데이터를 업데이트하는 로직
    private void updateSessionRecordWithAudioData(SessionRecord sessionRecord, 
                                                 String audioFileUrl, 
                                                 String transcribedText, 
                                                 String summary) {
        sessionRecord.setAudioFileUrl(audioFileUrl);
        sessionRecord.setTranscribedText(transcribedText);
        sessionRecord.setSummary(summary);
    }
}