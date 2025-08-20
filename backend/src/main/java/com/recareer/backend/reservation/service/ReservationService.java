package com.recareer.backend.reservation.service;

import com.recareer.backend.reservation.dto.ReservationListResponseDto;
import com.recareer.backend.reservation.dto.ReservationCancelRequestDto;
import com.recareer.backend.reservation.dto.ReservationRequestDto;
import com.recareer.backend.reservation.dto.ReservationResponseDto;
import com.recareer.backend.reservation.dto.ReservationUpdateRequestDto;
import com.recareer.backend.reservation.entity.Reservation;
import java.util.List;

public interface ReservationService {

  List<ReservationResponseDto> findAllReservationsByUserId(Long userId);

  Long createReservation(ReservationRequestDto requestDto);

  Reservation findById(Long reservationId);
  
  void updateReservationStatus(Long reservationId, ReservationUpdateRequestDto updateRequestDto);
  
  void cancelReservation(Long reservationId, ReservationCancelRequestDto cancelRequestDto);

  List<ReservationListResponseDto> getReservationsByUserId(Long userId, String status);
}
