package com.recareer.backend.reservation.service;

import com.recareer.backend.reservation.dto.ReservationCancelRequestDto;
import com.recareer.backend.reservation.dto.ReservationRequestDto;
import com.recareer.backend.reservation.dto.ReservationResponseDto;
import com.recareer.backend.reservation.entity.Reservation;
import com.recareer.backend.reservation.entity.ReservationStatus;
import java.util.List;

public interface ReservationService {

  List<ReservationResponseDto> findAllReservationsByUserId(Long userId);

  Long createReservation(ReservationRequestDto requestDto);

  Reservation findById(Long reservationId);
  
  void acceptReservation(Long reservationId);
  
  void completeReservation(Long reservationId);
  
  void cancelReservation(Long reservationId, ReservationCancelRequestDto cancelRequestDto);
}
