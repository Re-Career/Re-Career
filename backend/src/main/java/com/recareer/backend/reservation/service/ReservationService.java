package com.recareer.backend.reservation.service;

import com.recareer.backend.reservation.entity.Reservation;
import java.util.List;

public interface ReservationService {

  List<Reservation> findAllReservationsByUserId(Long userId);

  void createReservation(Reservation reservation);
}
