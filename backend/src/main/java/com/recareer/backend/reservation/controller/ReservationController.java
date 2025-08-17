package com.recareer.backend.reservation.controller;

import com.recareer.backend.reservation.dto.ReservationCancelRequestDto;
import com.recareer.backend.reservation.dto.ReservationRequestDto;
import com.recareer.backend.reservation.dto.ReservationResponseDto;
import com.recareer.backend.reservation.dto.ReservationUpdateRequestDto;
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

  @GetMapping
  @Operation(summary = "예약 목록 조회", description = "특정 유저의 모든 멘토링 예약 목록을 조회합니다.")
  public ResponseEntity<ApiResponse<List<ReservationResponseDto>>> getReservations(
      @RequestHeader("Authorization") String accessToken,
      @RequestParam Long userId) {
    
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
  
  @PostMapping("/{id}")
  @Operation(summary = "멘토링 상태 업데이트", description = "멘토링 상태를 업데이트합니다")
  public ResponseEntity<ApiResponse<String>> updateReservationStatus(
      @RequestHeader("Authorization") String accessToken,
      @PathVariable Long id,
      @Valid @RequestBody ReservationUpdateRequestDto updateRequestDto) {
    
    try {
      Long userId = authUtil.validateTokenAndGetUserId(accessToken);
      Reservation reservation = reservationService.findById(id);
      
      // 해당 멘토링의 멘토만 상태 변경 가능
      if (!reservation.isMentorParticipant(userId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("해당 멘토링의 멘토만 상태를 변경할 수 있습니다"));
      }
      
      reservationService.updateReservationStatus(id, updateRequestDto);
      
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

  @PostMapping("/{id}/cancel")
  @Operation(summary = "멘토링 예약 취소", description = "멘티가 자신의 멘토링 예약을 취소합니다")
  public ResponseEntity<ApiResponse<String>> cancelReservation(
      @RequestHeader("Authorization") String accessToken,
      @PathVariable Long id,
      @Valid @RequestBody ReservationCancelRequestDto cancelRequestDto) {
    
    try {
      Long userId = authUtil.validateTokenAndGetUserId(accessToken);
      Reservation reservation = reservationService.findById(id);
      
      // 해당 멘토링의 멘티만 취소 가능
      if (!reservation.isMenteeParticipant(userId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("본인의 예약만 취소할 수 있습니다"));
      }
      
      reservationService.cancelReservation(id, cancelRequestDto);
      
      return ResponseEntity.ok(ApiResponse.success("멘토링 예약이 취소되었습니다."));
      
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error(e.getMessage()));
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(ApiResponse.error(e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("예약 취소 중 오류가 발생했습니다."));
    }
  }
}
