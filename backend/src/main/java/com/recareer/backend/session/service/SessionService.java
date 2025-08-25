package com.recareer.backend.session.service;

import com.recareer.backend.session.dto.SessionCreateResponseDto;
import com.recareer.backend.session.dto.SessionListResponseDto;
import com.recareer.backend.session.dto.SessionCancelRequestDto;
import com.recareer.backend.session.dto.SessionRequestDto;
import com.recareer.backend.session.dto.SessionResponseDto;
import com.recareer.backend.session.dto.SessionUpdateRequestDto;
import com.recareer.backend.session.dto.SessionFeedbackRequestDto;
import com.recareer.backend.session.dto.SessionDetailResponseDto;
import com.recareer.backend.session.entity.Session;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface SessionService {

  List<SessionResponseDto> findSessionsByMentorId(Long mentorId);
  
  List<SessionResponseDto> findSessionsByMenteeId(Long menteeId);

  SessionCreateResponseDto createSession(SessionRequestDto requestDto, Long userId);

  Session findById(Long sessionId);
  
  void updateSessionStatus(Long sessionId, SessionUpdateRequestDto updateRequestDto);
  
  // void cancelSession(Long sessionId, SessionCancelRequestDto cancelRequestDto); // updateSessionStatus로 통합

  List<SessionListResponseDto> getSessionsByUserId(Long userId, String status);
  
  Long addSessionFeedback(Long sessionId, SessionFeedbackRequestDto requestDto);
  
  Long uploadSessionAudio(Long sessionId, MultipartFile audioFile);
  
  SessionDetailResponseDto getSessionDetailForMentee(Long sessionId);
  
  SessionDetailResponseDto getSessionDetailForMentor(Long sessionId);
}