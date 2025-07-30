package com.recareer.backend.reservation.controller;

import com.recareer.backend.reservation.dto.ReservationRequestDto;
import com.recareer.backend.reservation.entity.Reservation;
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
  @Operation(summary = "유저 별 멘토링 예약 리스트 조회")
  public ResponseEntity<ApiResponse<List<Reservation>>> getReservationsByUserId(@PathVariable Long userId) {
    List<Reservation> reservations = reservationService.findAllReservationsByUserId(userId);

    return ResponseEntity.ok(ApiResponse.success(reservations));
  }

  @PostMapping
  @Operation(summary = "멘토링 예약 생성")
  public ResponseEntity<ApiResponse<Long>> createReservation(@Valid @RequestBody ReservationRequestDto requestDto) {
    Long newReservationId = reservationService.createReservation(requestDto);

    return ResponseEntity.ok(ApiResponse.success(newReservationId));
  }
}
