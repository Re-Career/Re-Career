package com.recareer.backend.session.service;

import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.repository.MentorRepository;
import com.recareer.backend.session.dto.SessionCreateResponseDto;
import com.recareer.backend.session.dto.SessionListResponseDto;
import com.recareer.backend.session.dto.SessionCancelRequestDto;
import com.recareer.backend.session.dto.SessionRequestDto;
import com.recareer.backend.session.dto.SessionResponseDto;
import com.recareer.backend.session.dto.SessionUpdateRequestDto;
import com.recareer.backend.session.entity.Session;
import com.recareer.backend.session.entity.SessionStatus;
import com.recareer.backend.session.repository.SessionRepository;
import com.recareer.backend.user.entity.User;
import com.recareer.backend.user.repository.UserRepository;
import com.recareer.backend.session.dto.SessionFeedbackRequestDto;
import com.recareer.backend.session.dto.SessionDetailResponseDto;
import com.recareer.backend.common.service.S3Service;
import com.recareer.backend.common.service.AudioTranscriptionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
  private final SessionRepository sessionRepository;
  private final UserRepository userRepository;
  private final MentorRepository mentorRepository;
  private final S3Service s3Service;
  private final AudioTranscriptionService audioTranscriptionService;

  @Override
  @Transactional(readOnly = true)
  public List<SessionResponseDto> findAllSessionsByUserId(Long userId) {
    List<Session> sessions = sessionRepository.findAllByUserId(userId);
    return sessions.stream()
            .map(SessionResponseDto::from)
            .toList();
  }
  
  @Override
  @Transactional(readOnly = true)
  public List<SessionResponseDto> findSessionsByMentorId(Long mentorId) {
    List<Session> sessions = sessionRepository.findAllByMentorUserId(mentorId);
    return sessions.stream()
            .map(SessionResponseDto::from)
            .toList();
  }
  
  @Override
  @Transactional(readOnly = true)
  public List<SessionResponseDto> findSessionsByMenteeId(Long menteeId) {
    List<Session> sessions = sessionRepository.findAllByUserId(menteeId);
    return sessions.stream()
            .map(SessionResponseDto::from)
            .toList();
  }

  @Override
  @Transactional()
  public SessionCreateResponseDto createSession(SessionRequestDto requestDto, Long userId) {
    Mentor mentor = mentorRepository.findById(requestDto.getMentorId())
        .orElseThrow(() -> new EntityNotFoundException("해당 멘토를 찾을 수 없습니다."));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));

    Session session = Session.builder()
        .mentor(mentor)
        .user(user)
        .sessionTime(requestDto.getSessionTime())
        .status(SessionStatus.REQUESTED)
        .build();

    Session newSession = sessionRepository.save(session);

    return SessionCreateResponseDto.from(newSession);
    }

  @Override
  @Transactional(readOnly = true)
  public Session findById(Long sessionId) {
    return sessionRepository.findById(sessionId)
            .orElseThrow(() -> new EntityNotFoundException("해당 세션을 찾을 수 없습니다."));
  }
  

  @Override
  @Transactional
  public void updateSessionStatus(Long sessionId, SessionUpdateRequestDto updateRequestDto) {
    Session session = findById(sessionId);
    SessionStatus newStatus = updateRequestDto.getStatus();
    
    // 상태별 비즈니스 로직 검증 및 처리
    switch (newStatus) {
      case CONFIRMED -> {
        if (session.getStatus() != SessionStatus.REQUESTED) {
          throw new IllegalStateException("요청된 상태의 세션만 수락할 수 있습니다.");
        }
        session.setStatus(SessionStatus.CONFIRMED);
      }
      case COMPLETED -> {
        if (session.getStatus() != SessionStatus.CONFIRMED) {
          throw new IllegalStateException("확인된 상태의 세션만 완료할 수 있습니다.");
        }
        session.setStatus(SessionStatus.COMPLETED);
      }
      case CANCELED -> {
        if (session.getStatus() == SessionStatus.COMPLETED) {
          throw new IllegalStateException("완료된 멘토링은 취소할 수 없습니다.");
        }
        session.setStatus(SessionStatus.CANCELED);
        // 취소 사유는 선택사항
        if (updateRequestDto.getCancelReason() != null && !updateRequestDto.getCancelReason().trim().isEmpty()) {
          session.setCancelReason(updateRequestDto.getCancelReason());
        }
      }
      default -> throw new IllegalArgumentException("지원하지 않는 상태입니다: " + newStatus);
    }
    
    sessionRepository.save(session);
  }

  // @Override
  // @Transactional  
  // public void cancelSession(Long sessionId, SessionCancelRequestDto cancelRequestDto) {
  //   // updateSessionStatus로 통합됨 - status=CANCELED, cancelReason을 포함한 SessionUpdateRequestDto 사용
  // }

    @Override
    public List<SessionListResponseDto> getSessionsByUserId(Long userId, String status) {
        List<Session> sessions = sessionRepository.findAllByUserId(userId);

        if (status != null && !status.isBlank()) {
            if ("COMPLETED".equalsIgnoreCase(status)) {
                sessions = sessions.stream()
                        .filter(r -> r.getStatus() == SessionStatus.COMPLETED)
                        .collect(Collectors.toList());
            } else if ("SCHEDULED".equalsIgnoreCase(status)) {
                sessions = sessions.stream()
                        .filter(r -> r.getStatus() == SessionStatus.REQUESTED ||
                                     r.getStatus() == SessionStatus.CONFIRMED ||
                                     r.getStatus() == SessionStatus.CANCELED)
                        .collect(Collectors.toList());
            }
        }

        return sessions.stream()
                .map(SessionListResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long addSessionFeedback(Long sessionId, SessionFeedbackRequestDto requestDto) {
        Session session = findById(sessionId);
        session.setMenteeFeedback(requestDto.getMenteeFeedback());
        sessionRepository.save(session);
        return session.getId();
    }

    @Override
    @Transactional
    public Long uploadSessionAudio(Long sessionId, MultipartFile audioFile) {
        try {
            log.info("오디오 파일 처리 시작 - 세션 ID: {}, 파일명: {}", sessionId, audioFile.getOriginalFilename());

            Session session = findById(sessionId);

            // 1. S3에 오디오 파일 업로드
            String audioFileUrl = s3Service.uploadAudioFile(audioFile);
            log.info("S3 업로드 완료: {}", audioFileUrl);

            // 2. 음성을 텍스트로 전사
            String transcribedText = audioTranscriptionService.transcribeAudio(audioFile);
            log.info("음성 전사 완료");

            // 3. 전사된 텍스트 요약
            String summary = audioTranscriptionService.summarizeText(transcribedText);
            log.info("텍스트 요약 완료");

            // 4. 세션에 오디오 관련 데이터 저장
            session.setAudioFileUrl(audioFileUrl);
            session.setTranscribedText(transcribedText);
            session.setSummary(summary);

            Session savedSession = sessionRepository.save(session);
            log.info("세션 오디오 데이터 저장 완료 - ID: {}", savedSession.getId());

            return savedSession.getId();

        } catch (Exception e) {
            log.error("오디오 파일 처리 중 오류 발생: ", e);
            throw new RuntimeException("오디오 파일 처리에 실패했습니다.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SessionDetailResponseDto getSessionDetail(Long sessionId) {
        Session session = findById(sessionId);
        return SessionDetailResponseDto.from(session);
    }
}