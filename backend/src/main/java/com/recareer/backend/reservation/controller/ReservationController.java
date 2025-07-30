package com.recareer.backend.reservation.controller;

import com.recareer.backend.reservation.entity.Reservation;
import com.recareer.backend.reservation.service.ReservationService;
import com.recareer.backend.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  @GetMapping("/user/{userId}")
  public ResponseEntity<ApiResponse<List<Reservation>>> getReservationsByUserId(@PathVariable Long userId) {
    List<Reservation> reservations = reservationService.findAllReservationsByUserId(userId);

    return ResponseEntity.ok(ApiResponse.success(reservations));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<String>> createReservation(@RequestBody Reservation reservation) {
    reservationService.createReservation(reservation);

    return ResponseEntity.ok(ApiResponse.success("예약이 완료되었습니다."));
  }
}
