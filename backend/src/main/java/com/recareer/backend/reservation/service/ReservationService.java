package com.recareer.backend.reservation.service;

import com.recareer.backend.reservation.dto.ReservationRequestDto;
import com.recareer.backend.reservation.dto.ReservationResponseDto;
import com.recareer.backend.reservation.entity.Reservation;
import java.util.List;

public interface ReservationService {

  List<ReservationResponseDto> findAllReservationsByUserId(Long userId);

  Long createReservation(ReservationRequestDto requestDto);
}
