package com.recareer.backend.mentoringRecord.service;

import com.recareer.backend.common.service.S3Service;
import com.recareer.backend.mentoringRecord.dto.MentoringRecordRequestDto;
import com.recareer.backend.mentoringRecord.dto.MentoringRecordResponseDto;
import com.recareer.backend.mentoringRecord.entity.MentoringRecord;
import com.recareer.backend.mentoringRecord.entity.MentoringRecordStatus;
import com.recareer.backend.mentoringRecord.repository.MentoringRecordRepository;
import com.recareer.backend.reservation.entity.Reservation;
import com.recareer.backend.reservation.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentoringRecordServiceImpl implements MentoringRecordService {

    private final MentoringRecordRepository mentoringRecordRepository;
    private final ReservationRepository reservationRepository;
    private final S3Service s3Service;
    private final AudioTranscriptionService audioTranscriptionService;

    @Override
    @Transactional(readOnly = true)
    public MentoringRecord findMentoringRecordById(Long id) {
        return mentoringRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 멘토링 기록을 찾을 수 없습니다."));
    }

    @Override
    @Transactional
    public Long createOrUpdateMentoringRecord(Long reservationId, MentoringRecordRequestDto requestDto) {
        MentoringRecord mentoringRecord = findOrCreateMentoringRecord(reservationId);
        mentoringRecord.setMenteeFeedback(requestDto.getMenteeFeedback());
        
        // 피드백 작성 완료 시 상태 업데이트
        if (mentoringRecord.getAudioFileUrl() != null) {
            mentoringRecord.setStatus(MentoringRecordStatus.ALL_COMPLETED);
        } else {
            mentoringRecord.setStatus(MentoringRecordStatus.FEEDBACK_PENDING);
        }
        
        MentoringRecord savedMentoringRecord = mentoringRecordRepository.save(mentoringRecord);
        return savedMentoringRecord.getId();
    }

    @Override
    @Transactional
    public Long uploadAudioAndProcess(Long reservationId, MultipartFile audioFile) {
        try {
            log.info("오디오 파일 처리 시작 - 예약 ID: {}, 파일명: {}", reservationId, audioFile.getOriginalFilename());

            // 1. S3에 오디오 파일 업로드
            String audioFileUrl = s3Service.uploadAudioFile(audioFile);
            log.info("S3 업로드 완료: {}", audioFileUrl);
            
            // 상태를 AUDIO_PROCESSING으로 변경
            MentoringRecord mentoringRecord = findOrCreateMentoringRecord(reservationId);
            mentoringRecord.setStatus(MentoringRecordStatus.AUDIO_PROCESSING);

            mentoringRecordRepository.save(mentoringRecord);

            // 2. 음성을 텍스트로 전사
            String transcribedText = audioTranscriptionService.transcribeAudio(audioFile);
            log.info("음성 전사 완료");

            // 3. 전사된 텍스트 요약
            String summary = audioTranscriptionService.summarizeText(transcribedText);
            log.info("텍스트 요약 완료");

            // 4. 멘토링 기록 업데이트
            updateMentoringRecordWithAudioData(mentoringRecord, audioFileUrl, transcribedText, summary);
            
            // 오디오 처리 완료 상태로 업데이트
            if (mentoringRecord.getMenteeFeedback() != null) {
                mentoringRecord.setStatus(MentoringRecordStatus.ALL_COMPLETED);
            } else {
                mentoringRecord.setStatus(MentoringRecordStatus.FEEDBACK_PENDING);
            }

            MentoringRecord savedMentoringRecord = mentoringRecordRepository.save(mentoringRecord);
            log.info("멘토링 기록 저장 완료 - ID: {}", savedMentoringRecord.getId());

            return savedMentoringRecord.getId();

        } catch (Exception e) {
            log.error("오디오 파일 처리 중 오류 발생: ", e);
            throw new RuntimeException("오디오 파일 처리에 실패했습니다.", e);
        }
    }

    // 멘토링 기록을 찾거나 새로 생성하는 공통 로직
    private MentoringRecord findOrCreateMentoringRecord(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 예약을 찾을 수 없습니다."));

        return mentoringRecordRepository.findByReservationId(reservationId)
                .orElse(MentoringRecord.builder()
                        .reservation(reservation)
                        .status(MentoringRecordStatus.AUDIO_PENDING)
                        .build());
    }

    // 멘토링 기록에 오디오 관련 데이터를 업데이트하는 로직
    private void updateMentoringRecordWithAudioData(MentoringRecord mentoringRecord, 
                                                   String audioFileUrl, 
                                                   String transcribedText, 
                                                   String summary) {
        mentoringRecord.setAudioFileUrl(audioFileUrl);
        mentoringRecord.setTranscribedText(transcribedText);
        mentoringRecord.setSummary(summary);
    }
    
    @Override
    @Transactional
    public void updateMentoringRecordStatus(Long mentoringRecordId, MentoringRecordStatus status) {
        MentoringRecord mentoringRecord = findMentoringRecordById(mentoringRecordId);
        mentoringRecord.setStatus(status);

        mentoringRecordRepository.save(mentoringRecord);
    }
    
    @Override
    @Transactional(readOnly = true)
    public MentoringRecord findByReservationId(Long reservationId) {
        return mentoringRecordRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 예약에 대한 멘토링 기록을 찾을 수 없습니다."));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MentoringRecordResponseDto> findCompletedMentoringRecordsByUserId(Long userId) {
        List<MentoringRecord> mentoringRecords = mentoringRecordRepository.findCompletedMentoringRecordsByUserId(userId);

        return mentoringRecords.stream()
                .map(MentoringRecordResponseDto::from)
                .collect(Collectors.toList());
    }
}