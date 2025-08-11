package com.recareer.backend.reservation.controller;

import com.recareer.backend.reservation.dto.ReservationCancelRequestDto;
import com.recareer.backend.reservation.dto.ReservationRequestDto;
import com.recareer.backend.reservation.dto.ReservationResponseDto;
import com.recareer.backend.reservation.entity.Reservation;
import com.recareer.backend.reservation.service.ReservationService;
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
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservation", description = "예약 관련 API")
public class ReservationController {

  private final ReservationService reservationService;
  private final AuthUtil authUtil;

  @GetMapping("/user/{userId}")
  @Operation(summary = "예정된 상담 리스트 조회", description = "특정 유저의 모든 멘토링 예약 목록을 조회합니다.")
  public ResponseEntity<ApiResponse<List<ReservationResponseDto>>> getReservationsByUserId(
      @RequestHeader("Authorization") String accessToken,
      @PathVariable Long userId) {
    
    try {
      Long requestUserId = authUtil.validateTokenAndGetUserId(accessToken);
      
      // 자신의 예약 목록만 조회 가능
      if (!requestUserId.equals(userId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("본인의 예약 목록만 조회할 수 있습니다"));
      }
      
      List<ReservationResponseDto> reservations = reservationService.findAllReservationsByUserId(userId);
      return ResponseEntity.ok(ApiResponse.success(reservations));
      
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  @PostMapping
  @Operation(summary = "멘토링 예약 생성")
  public ResponseEntity<ApiResponse<Long>> createReservation(
      @RequestHeader("Authorization") String accessToken,
      @Valid @RequestBody ReservationRequestDto requestDto) {
    
    try {
      Long userId = authUtil.validateTokenAndGetUserId(accessToken);
      
      // 요청 DTO의 userId와 토큰의 userId가 일치하는지 확인
      if (!userId.equals(requestDto.getUserId())) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("본인의 이름으로만 예약할 수 있습니다"));
      }
      
      Long newReservationId = reservationService.createReservation(requestDto);
      return ResponseEntity.ok(ApiResponse.success(newReservationId));
      
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error(e.getMessage()));
    }
  }
  
  @PostMapping("/{reservationId}/accept")
  @Operation(summary = "멘토링 수락", description = "멘토가 멘토링 요청을 수락합니다. 상태가 CONFIRMED로 변경됩니다.")
  public ResponseEntity<ApiResponse<String>> acceptReservation(
      @RequestHeader("Authorization") String accessToken,
      @PathVariable Long reservationId) {
    
    try {
      Long userId = authUtil.validateTokenAndGetUserId(accessToken);
      Reservation reservation = reservationService.findById(reservationId);
      
      // 해당 멘토링의 멘토만 수락 가능
      if (!reservation.isMentorParticipant(userId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("해당 멘토링의 멘토만 수락할 수 있습니다"));
      }
      
      reservationService.acceptReservation(reservationId);
      return ResponseEntity.ok(ApiResponse.success("멘토링이 수락되었습니다."));
      
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error(e.getMessage()));
    }
  }
  
  @PostMapping("/{reservationId}/complete")
  @Operation(summary = "멘토링 완료", description = "멘토링을 완료 처리합니다. 상태가 COMPLETED로 변경되고 멘토링 기록이 생성됩니다.")
  public ResponseEntity<ApiResponse<String>> completeReservation(
      @RequestHeader("Authorization") String accessToken,
      @PathVariable Long reservationId) {
    
    try {
      Long userId = authUtil.validateTokenAndGetUserId(accessToken);
      Reservation reservation = reservationService.findById(reservationId);
      
      // 해당 멘토링의 멘토만 완료 처리 가능
      if (!reservation.isMentorParticipant(userId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("해당 멘토링의 멘토만 완료 처리할 수 있습니다"));
      }
      
      reservationService.completeReservation(reservationId);
      return ResponseEntity.ok(ApiResponse.success("멘토링이 완료되었습니다."));
      
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error(e.getMessage()));
    }
  }
  
  @PostMapping("/{reservationId}/cancel")
  @Operation(summary = "멘토링 취소", description = "멘토링을 취소합니다. 상태가 CANCELED로 변경되고 취소 사유가 저장됩니다.")
  public ResponseEntity<ApiResponse<String>> cancelReservation(
      @RequestHeader("Authorization") String accessToken,
      @PathVariable Long reservationId,
      @Valid @RequestBody ReservationCancelRequestDto cancelRequestDto) {
    
    try {
      Long userId = authUtil.validateTokenAndGetUserId(accessToken);
      Reservation reservation = reservationService.findById(reservationId);
      
      // 해당 멘토링의 멘토만 취소 가능
      if (!reservation.isMentorParticipant(userId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("해당 멘토링의 멘토만 취소할 수 있습니다"));
      }
      
      reservationService.cancelReservation(reservationId, cancelRequestDto);
      return ResponseEntity.ok(ApiResponse.success("멘토링이 취소되었습니다."));
      
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error(e.getMessage()));
    }
  }
}
