package com.recareer.backend.session.controller;

import com.recareer.backend.mentor.repository.MentorRepository;
import com.recareer.backend.session.dto.SessionCancelRequestDto;
import com.recareer.backend.session.dto.SessionCreateResponseDto;
import com.recareer.backend.session.dto.SessionRequestDto;
import com.recareer.backend.session.dto.SessionResponseDto;
import com.recareer.backend.session.dto.SessionUpdateRequestDto;
import com.recareer.backend.session.dto.SessionUpdateResponseDto;
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
@RequestMapping("/sessions")
@RequiredArgsConstructor
@Tag(name = "Session", description = "세션 관련 API")
public class SessionController {

  private final SessionService sessionService;
  private final AuthUtil authUtil;
  private final MentorRepository mentorRepository;

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
        // 실제로 해당 userId가 멘토인지 검증
        if (mentorRepository.findByUserId(userId).isEmpty()) {
          return ResponseEntity.status(HttpStatus.FORBIDDEN)
              .body(ApiResponse.error("멘토로 등록되지 않은 사용자입니다"));
        }
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
  public ResponseEntity<ApiResponse<SessionCreateResponseDto>> createSession(
      @RequestHeader("Authorization") String accessToken,
      @Valid @RequestBody SessionRequestDto requestDto) {
    
    try {
      Long userId = authUtil.validateTokenAndGetUserId(accessToken);
      
      SessionCreateResponseDto response = sessionService.createSession(requestDto, userId);
      return ResponseEntity.ok(ApiResponse.success(response));
      
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error(e.getMessage()));
    }
  }
  
  @PatchMapping("/{id}")
  @Operation(
      summary = "멘토링 상태 업데이트", 
      description = """
          ### 🔹 상태 전환 규칙
          - `REQUESTED` → `CONFIRMED` (멘토가 세션 수락)
          - `REQUESTED` → `CANCELED` (멘토가 세션 거절)  
          - `CONFIRMED` → `COMPLETED` (멘토가 세션 완료 처리)
          - `CONFIRMED` → `CANCELED` (멘토가 세션 취소)
          - `COMPLETED`는 더 이상 변경 불가
          
          ### 🔹 권한
          - **멘토만** 상태 변경 가능 (해당 세션의 멘토가 아니면 403 에러)
          """
  )
  public ResponseEntity<ApiResponse<SessionUpdateResponseDto>> updateSessionStatus(
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
      
      // 업데이트된 세션 정보를 조회하여 응답에 포함
      Session updatedSession = sessionService.findById(id);
      SessionUpdateResponseDto responseDto = SessionUpdateResponseDto.from(updatedSession);
      
      return ResponseEntity.ok(ApiResponse.success(responseDto));
      
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
  @Operation(summary = "상담 상세 조회")
  public ResponseEntity<ApiResponse<SessionDetailResponseDto>> getSessionDetail(
      @RequestHeader("Authorization") String accessToken,
      @PathVariable Long id,
      @RequestParam(required = true) String role) {
    
    try {
      Long userId = authUtil.validateTokenAndGetUserId(accessToken);
      Session session = sessionService.findById(id);
      
      // role 검증
      if (!"MENTOR".equals(role) && !"MENTEE".equals(role)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("역할은 MENTOR 또는 MENTEE만 가능합니다"));
      }
      
      SessionDetailResponseDto responseDto;
      if ("MENTOR".equals(role)) {
        // MENTOR로 요청한 경우: 실제로 멘토인지 검증하고 멘티 정보 반환
        if (!session.isMentorParticipant(userId)) {
          return ResponseEntity.status(HttpStatus.FORBIDDEN)
              .body(ApiResponse.error("해당 세션의 멘토가 아닙니다"));
        }
        responseDto = sessionService.getSessionDetailForMentor(id);
      } else {
        // MENTEE로 요청한 경우: 실제로 멘티인지 검증하고 멘토 정보 반환
        if (!session.isMenteeParticipant(userId)) {
          return ResponseEntity.status(HttpStatus.FORBIDDEN)
              .body(ApiResponse.error("해당 세션의 멘티가 아닙니다"));
        }
        responseDto = sessionService.getSessionDetailForMentee(id);
      }

      return ResponseEntity.ok(ApiResponse.success(responseDto));
      
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  // @PostMapping("/{id}/feedback")
  // @Operation(summary = "세션 피드백 작성", description = "멘티가 세션 후 멘토에 대한 피드백을 작성합니다.")
  // public ResponseEntity<ApiResponse<Long>> addSessionFeedback(
  //     @RequestHeader("Authorization") String accessToken,
  //     @PathVariable Long id,
  //     @Valid @RequestBody SessionFeedbackRequestDto requestDto) {
  //   
  //   try {
  //     Long userId = authUtil.validateTokenAndGetUserId(accessToken);
  //     Session session = sessionService.findById(id);
  //     
  //     if (!session.isMenteeParticipant(userId)) {
  //       return ResponseEntity.status(HttpStatus.FORBIDDEN)
  //           .body(ApiResponse.error("해당 세션에 참여한 멘티만 피드백을 작성할 수 있습니다"));
  //     }
  //     
  //     Long sessionId = sessionService.addSessionFeedback(id, requestDto);
  //     return ResponseEntity.ok(ApiResponse.success(sessionId));
  //     
  //   } catch (IllegalArgumentException e) {
  //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
  //         .body(ApiResponse.error(e.getMessage()));
  //   }
  // }

  // @PostMapping("/{id}/audio")
  // @Operation(summary = "세션 녹음 파일 업로드",
  //            description = "멘토가 세션 녹음 파일을 업로드하면 S3에 저장하고, " +
  //                        "AI를 통해 음성을 텍스트로 전사하고 상담 내용을 요약합니다.")
  // public ResponseEntity<ApiResponse<Long>> uploadSessionAudio(
  //     @RequestHeader("Authorization") String accessToken,
  //     @PathVariable Long id,
  //     @RequestParam("audioFile") MultipartFile audioFile) {
  //   
  //   try {
  //     Long userId = authUtil.validateTokenAndGetUserId(accessToken);
  //     Session session = sessionService.findById(id);
  //     
  //     if (!session.isMentorParticipant(userId)) {
  //       return ResponseEntity.status(HttpStatus.FORBIDDEN)
  //           .body(ApiResponse.error("해당 세션에 참여한 멘토만 음성 파일을 업로드할 수 있습니다"));
  //     }
  //     
  //     Long sessionId = sessionService.uploadSessionAudio(id, audioFile);
  //     return ResponseEntity.ok(ApiResponse.success(sessionId));
  //     
  //   } catch (IllegalArgumentException e) {
  //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
  //         .body(ApiResponse.error(e.getMessage()));
  //   }
  // }
}