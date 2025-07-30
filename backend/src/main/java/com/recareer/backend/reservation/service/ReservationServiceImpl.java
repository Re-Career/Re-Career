package com.recareer.backend.reservation.service;

import com.recareer.backend.reservation.entity.Reservation;
import com.recareer.backend.reservation.repository.ReservationRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
  private final ReservationRepository reservationRepository;

  @Override
  public List<Reservation> findAllReservationsByUserId(Long userId) {
    return reservationRepository.findAllByUserId(userId);
  }

  @Override
  public void createReservation(Reservation reservation) {
    if (reservation == null) {
      throw new IllegalArgumentException("예약 정보가 없습니다.");
    }

    if (reservation.getReservationTime().isBefore(LocalDateTime.now())) {
      throw new IllegalStateException("과거 시간에는 예약할 수 없습니다.");
    }

    reservationRepository.save(reservation);
  }
}
