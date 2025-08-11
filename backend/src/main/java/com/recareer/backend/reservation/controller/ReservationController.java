package com.recareer.backend.reservation.controller;

import com.recareer.backend.reservation.dto.ReservationCancelRequestDto;
import com.recareer.backend.reservation.dto.ReservationRequestDto;
import com.recareer.backend.reservation.dto.ReservationResponseDto;
import com.recareer.backend.reservation.entity.ReservationStatus;
import com.recareer.backend.reservation.service.ReservationService;
import com.recareer.backend.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservation", description = "예약 관련 API")
public class ReservationController {

  private final ReservationService reservationService;

  @GetMapping("/user/{userId}")
  @Operation(summary = "유저 별 멘토링 예약 리스트 조회", description = "특정 유저의 모든 멘토링 예약 목록을 조회합니다.")
  public ResponseEntity<ApiResponse<List<ReservationResponseDto>>> getReservationsByUserId(@PathVariable Long userId) {
    List<ReservationResponseDto> reservations = reservationService.findAllReservationsByUserId(userId);

    return ResponseEntity.ok(ApiResponse.success(reservations));
  }

  @PostMapping
  @Operation(summary = "멘토링 예약 생성")
  public ResponseEntity<ApiResponse<Long>> createReservation(@Valid @RequestBody ReservationRequestDto requestDto) {
    Long newReservationId = reservationService.createReservation(requestDto);

    return ResponseEntity.ok(ApiResponse.success(newReservationId));
  }
  
  @PostMapping("/{reservationId}/accept")
  @Operation(summary = "멘토링 수락", description = "멘토가 멘토링 요청을 수락합니다. 상태가 CONFIRMED로 변경됩니다.")
  public ResponseEntity<ApiResponse<String>> acceptReservation(@PathVariable Long reservationId) {
    reservationService.acceptReservation(reservationId);

    return ResponseEntity.ok(ApiResponse.success("멘토링이 수락되었습니다."));
  }
  
  @PostMapping("/{reservationId}/complete")
  @Operation(summary = "멘토링 완료", description = "멘토링을 완료 처리합니다. 상태가 COMPLETED로 변경되고 멘토링 기록이 생성됩니다.")
  public ResponseEntity<ApiResponse<String>> completeReservation(@PathVariable Long reservationId) {
    reservationService.completeReservation(reservationId);

    return ResponseEntity.ok(ApiResponse.success("멘토링이 완료되었습니다."));
  }
  
  @PostMapping("/{reservationId}/cancel")
  @Operation(summary = "멘토링 취소", description = "멘토링을 취소합니다. 상태가 CANCELED로 변경되고 취소 사유가 저장됩니다.")
  public ResponseEntity<ApiResponse<String>> cancelReservation(
      @PathVariable Long reservationId,
      @Valid @RequestBody ReservationCancelRequestDto cancelRequestDto) {
    
    reservationService.cancelReservation(reservationId, cancelRequestDto);

    return ResponseEntity.ok(ApiResponse.success("멘토링이 취소되었습니다."));
  }
}
