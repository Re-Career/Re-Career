package com.recareer.backend.reservation.repository;

import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.reservation.entity.Reservation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
  List<Reservation> findAllByUserId(Long userId);
  
  List<Reservation> findByMentor(Mentor mentor);
}
