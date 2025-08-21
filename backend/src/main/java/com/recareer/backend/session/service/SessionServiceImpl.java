package com.recareer.backend.session.service;

import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.repository.MentorRepository;
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
import com.recareer.backend.mentoringRecord.entity.MentoringRecord;
import com.recareer.backend.mentoringRecord.entity.MentoringRecordStatus;
import com.recareer.backend.mentoringRecord.repository.MentoringRecordRepository;
import jakarta.persistence.EntityNotFoundException;
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
  private final MentoringRecordRepository mentoringRecordRepository;

  @Override
  @Transactional(readOnly = true)
  public List<SessionResponseDto> findAllSessionsByUserId(Long userId) {
    List<Session> sessions = sessionRepository.findAllByUserId(userId);
    return sessions.stream()
            .map(SessionResponseDto::from)
            .toList();
  }

  @Override
  @Transactional()
  public Long createSession(SessionRequestDto requestDto) {
    Mentor mentor = mentorRepository.findById(requestDto.getMentorId())
        .orElseThrow(() -> new EntityNotFoundException("해당 멘토를 찾을 수 없습니다."));
    User user = userRepository.findById(requestDto.getUserId())
        .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));

    Session session = Session.builder()
        .mentor(mentor)
        .user(user)
        .sessionTime(requestDto.getSessionTime())
        .status(SessionStatus.REQUESTED)
        .build();

    Session newSession = sessionRepository.save(session);

    return newSession.getId();
    }

  @Override
  @Transactional(readOnly = true)
  public Session findById(Long sessionId) {
    return sessionRepository.findById(sessionId)
            .orElseThrow(() -> new EntityNotFoundException("해당 세션을 찾을 수 없습니다."));
  }
  
  
  private void createMentoringRecordIfNotExists(Session session) {
    // 이미 MentoringRecord가 존재하는지 확인
    boolean exists = mentoringRecordRepository.existsBySessionId(session.getId());
    
    if (!exists) {
      MentoringRecord mentoringRecord = MentoringRecord.builder()
          .session(session)
          .status(MentoringRecordStatus.AUDIO_PENDING)
          .build();
      
      mentoringRecordRepository.save(mentoringRecord);
    }
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
        // 멘토링 완료 시 자동으로 MentoringRecord 생성
        createMentoringRecordIfNotExists(session);
      }
      case CANCELED -> {
        if (session.getStatus() == SessionStatus.COMPLETED) {
          throw new IllegalStateException("완료된 멘토링은 취소할 수 없습니다.");
        }
        if (updateRequestDto.getCancelReason() == null || updateRequestDto.getCancelReason().trim().isEmpty()) {
          throw new IllegalArgumentException("취소 사유는 필수입니다.");
        }
        session.setStatus(SessionStatus.CANCELED);
        session.setCancelReason(updateRequestDto.getCancelReason());
      }
      default -> throw new IllegalArgumentException("지원하지 않는 상태입니다: " + newStatus);
    }
    
    sessionRepository.save(session);
  }

  @Override
  @Transactional
  public void cancelSession(Long sessionId, SessionCancelRequestDto cancelRequestDto) {
    Session session = findById(sessionId);
    
    // 완료된 멘토링은 취소할 수 없음
    if (session.getStatus() == SessionStatus.COMPLETED) {
      throw new IllegalStateException("완료된 멘토링은 취소할 수 없습니다.");
    }
    
    // 이미 취소된 멘토링은 중복 취소할 수 없음
    if (session.getStatus() == SessionStatus.CANCELED) {
      throw new IllegalStateException("이미 취소된 멘토링입니다.");
    }
    
    // 취소 사유 검증
    if (cancelRequestDto.getCancelReason() == null || cancelRequestDto.getCancelReason().trim().isEmpty()) {
      throw new IllegalArgumentException("취소 사유는 필수입니다.");
    }
    
    // 상태 변경
    session.setStatus(SessionStatus.CANCELED);
    session.setCancelReason(cancelRequestDto.getCancelReason());
    
    sessionRepository.save(session);
    
    log.info("세션 취소 완료 - 세션 ID: {}, 취소 사유: {}", sessionId, cancelRequestDto.getCancelReason());
  }

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
}