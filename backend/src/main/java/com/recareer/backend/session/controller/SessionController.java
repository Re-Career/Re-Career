package com.recareer.backend.session.controller;

import com.recareer.backend.session.dto.SessionCancelRequestDto;
import com.recareer.backend.session.dto.SessionRequestDto;
import com.recareer.backend.session.dto.SessionResponseDto;
import com.recareer.backend.session.dto.SessionUpdateRequestDto;
import com.recareer.backend.session.entity.Session;
import com.recareer.backend.session.service.SessionService;
import com.recareer.backend.session.dto.SessionFeedbackRequestDto;
import com.recareer.backend.session.dto.SessionDetailResponseDto;
import org.springframework.web.multipart.MultipartFile;
import com.recareer.backend.response.ApiResponse;
import com.recareer.backend.auth.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/my/sessions")
@RequiredArgsConstructor
@Tag(name = "Session", description = "세션 관련 API")
public class SessionController {

  private final SessionService sessionService;
  private final AuthUtil authUtil;

  @GetMapping
  @Operation(summary = "상담 내역 조회", description = "역할(MENTOR/MENTEE)에 따른 예정/완료된 상담 목록을 조회합니다.")
  public ResponseEntity<ApiResponse<List<SessionResponseDto>>> getSessions(
      @RequestHeader("Authorization") String accessToken,
      @RequestParam(required = true) String role) {
    
    try {
      Long userId = authUtil.validateTokenAndGetUserId(accessToken);
      
      // role 검증
      if (!"MENTOR".equals(role) && !"MENTEE".equals(role)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("역할은 MENTOR 또는 MENTEE만 가능합니다"));
      }
      
      List<SessionResponseDto> sessions;
      if ("MENTOR".equals(role)) {
        sessions = sessionService.findSessionsByMentorId(userId);
      } else {
        sessions = sessionService.findSessionsByMenteeId(userId);
      }
      
      return ResponseEntity.ok(ApiResponse.success(sessions));
      
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @PostMapping
  @Operation(summary = "멘토링 세션 생성")
  public ResponseEntity<ApiResponse<Long>> createSession(
      @RequestHeader("Authorization") String accessToken,
      @Valid @RequestBody SessionRequestDto requestDto) {
    
    try {
      Long userId = authUtil.validateTokenAndGetUserId(accessToken);
      
      // 요청 DTO의 userId와 토큰의 userId가 일치하는지 확인
      if (!userId.equals(requestDto.getUserId())) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("본인의 이름으로만 세션을 생성할 수 있습니다"));
      }
      
      Long newSessionId = sessionService.createSession(requestDto);
      return ResponseEntity.ok(ApiResponse.success(newSessionId));
      
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error(e.getMessage()));
    }
  }
  
  @PostMapping("/{id}")
  @Operation(summary = "멘토링 상태 업데이트", description = "멘토링 상태를 업데이트합니다")
  public ResponseEntity<ApiResponse<String>> updateSessionStatus(
      @RequestHeader("Authorization") String accessToken,
      @PathVariable Long id,
      @Valid @RequestBody SessionUpdateRequestDto updateRequestDto) {
    
    try {
      Long userId = authUtil.validateTokenAndGetUserId(accessToken);
      Session session = sessionService.findById(id);
      
      // 해당 멘토링의 멘토만 상태 변경 가능
      if (!session.isMentorParticipant(userId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("해당 멘토링의 멘토만 상태를 변경할 수 있습니다"));
      }
      
      sessionService.updateSessionStatus(id, updateRequestDto);
      
      String message = switch (updateRequestDto.getStatus()) {
        case CONFIRMED -> "멘토링이 수락되었습니다.";
        case COMPLETED -> "멘토링이 완료되었습니다.";
        case CANCELED -> "멘토링이 취소되었습니다.";
        default -> "멘토링 상태가 업데이트되었습니다.";
      };
      
      return ResponseEntity.ok(ApiResponse.success(message));
      
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  // @PostMapping("/{id}/cancel")
  // @Operation(summary = "멘토링 세션 취소", description = "멘티가 자신의 멘토링 세션을 취소합니다 - POST /{id}에서 status=CANCELED로 처리")
  // public ResponseEntity<ApiResponse<String>> cancelSession(
  //     @RequestHeader("Authorization") String accessToken,
  //     @PathVariable Long id,
  //     @Valid @RequestBody SessionCancelRequestDto cancelRequestDto) {
  //   
  //   try {
  //     Long userId = authUtil.validateTokenAndGetUserId(accessToken);
  //     Session session = sessionService.findById(id);
  //     
  //     // 해당 멘토링의 멘티만 취소 가능
  //     if (!session.isMenteeParticipant(userId)) {
  //       return ResponseEntity.status(HttpStatus.FORBIDDEN)
  //           .body(ApiResponse.error("본인의 세션만 취소할 수 있습니다"));
  //     }
  //     
  //     sessionService.cancelSession(id, cancelRequestDto);
  //     
  //     return ResponseEntity.ok(ApiResponse.success("멘토링 세션이 취소되었습니다."));
  //     
  //   } catch (IllegalArgumentException e) {
  //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
  //         .body(ApiResponse.error(e.getMessage()));
  //   } catch (IllegalStateException e) {
  //     return ResponseEntity.status(HttpStatus.BAD_REQUEST)
  //         .body(ApiResponse.error(e.getMessage()));
  //   } catch (Exception e) {
  //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
  //         .body(ApiResponse.error("세션 취소 중 오류가 발생했습니다."));
  //   }
  // }

  @GetMapping("/{id}")
  @Operation(summary = "완료된 상담 상세 조회")
  public ResponseEntity<ApiResponse<SessionDetailResponseDto>> getSessionDetail(
      @RequestHeader("Authorization") String accessToken,
      @PathVariable Long id) {
    
    try {
      Long userId = authUtil.validateTokenAndGetUserId(accessToken);
      Session session = sessionService.findById(id);
      
      // 해당 세션의 참여자만 조회 가능
      if (!session.isMentorParticipant(userId) && 
          !session.isMenteeParticipant(userId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("해당 세션 참여자만 조회할 수 있습니다"));
      }
      
      SessionDetailResponseDto responseDto = sessionService.getSessionDetail(id);

      return ResponseEntity.ok(ApiResponse.success(responseDto));
      
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @PostMapping("/{id}/feedback")
  @Operation(summary = "세션 피드백 작성", description = "멘티가 세션 후 멘토에 대한 피드백을 작성합니다.")
  public ResponseEntity<ApiResponse<Long>> addSessionFeedback(
      @RequestHeader("Authorization") String accessToken,
      @PathVariable Long id,
      @Valid @RequestBody SessionFeedbackRequestDto requestDto) {
    
    try {
      Long userId = authUtil.validateTokenAndGetUserId(accessToken);
      Session session = sessionService.findById(id);
      
      if (!session.isMenteeParticipant(userId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("해당 세션에 참여한 멘티만 피드백을 작성할 수 있습니다"));
      }
      
      Long sessionId = sessionService.addSessionFeedback(id, requestDto);
      return ResponseEntity.ok(ApiResponse.success(sessionId));
      
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @PostMapping("/{id}/audio")
  @Operation(summary = "세션 녹음 파일 업로드",
             description = "멘토가 세션 녹음 파일을 업로드하면 S3에 저장하고, " +
                         "AI를 통해 음성을 텍스트로 전사하고 상담 내용을 요약합니다.")
  public ResponseEntity<ApiResponse<Long>> uploadSessionAudio(
      @RequestHeader("Authorization") String accessToken,
      @PathVariable Long id,
      @RequestParam("audioFile") MultipartFile audioFile) {
    
    try {
      Long userId = authUtil.validateTokenAndGetUserId(accessToken);
      Session session = sessionService.findById(id);
      
      if (!session.isMentorParticipant(userId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("해당 세션에 참여한 멘토만 음성 파일을 업로드할 수 있습니다"));
      }
      
      Long sessionId = sessionService.uploadSessionAudio(id, audioFile);
      return ResponseEntity.ok(ApiResponse.success(sessionId));
      
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error(e.getMessage()));
    }
  }
}