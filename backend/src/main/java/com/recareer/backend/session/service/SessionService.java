package com.recareer.backend.session.service;

import com.recareer.backend.session.dto.SessionListResponseDto;
import com.recareer.backend.session.dto.SessionCancelRequestDto;
import com.recareer.backend.session.dto.SessionRequestDto;
import com.recareer.backend.session.dto.SessionResponseDto;
import com.recareer.backend.session.dto.SessionUpdateRequestDto;
import com.recareer.backend.session.entity.Session;
import java.util.List;

public interface SessionService {

  List<SessionResponseDto> findAllSessionsByUserId(Long userId);
  
  List<SessionResponseDto> findSessionsByMentorId(Long mentorId);
  
  List<SessionResponseDto> findSessionsByMenteeId(Long menteeId);

  Long createSession(SessionRequestDto requestDto);

  Session findById(Long sessionId);
  
  void updateSessionStatus(Long sessionId, SessionUpdateRequestDto updateRequestDto);
  
  void cancelSession(Long sessionId, SessionCancelRequestDto cancelRequestDto);

  List<SessionListResponseDto> getSessionsByUserId(Long userId, String status);
}