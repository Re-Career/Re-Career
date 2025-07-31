package com.recareer.backend.reservation.service;

import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.repository.MentorRepository;
import com.recareer.backend.reservation.dto.ReservationRequestDto;
import com.recareer.backend.reservation.entity.Reservation;
import com.recareer.backend.reservation.repository.ReservationRepository;
import com.recareer.backend.user.entity.User;
import com.recareer.backend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
  private final ReservationRepository reservationRepository;
  private final UserRepository userRepository;
  private final MentorRepository mentorRepository;

  @Override
  @Transactional(readOnly = true)
  public List<Reservation> findAllReservationsByUserId(Long userId) {
    return reservationRepository.findAllByUserId(userId);
  }

  @Override
  @Transactional()
  public Long createReservation(ReservationRequestDto requestDto) {
    Mentor mentor = mentorRepository.findById(requestDto.getMentorId())
        .orElseThrow(() -> new EntityNotFoundException("해당 멘토를 찾을 수 없습니다."));
    User user = userRepository.findById(requestDto.getUserId())
        .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));

    Reservation reservation = Reservation.builder()
        .mentor(mentor)
        .user(user)
        .reservationTime(requestDto.getReservationTime())
        .build();

    Reservation newReservation = reservationRepository.save(reservation);

    return newReservation.getId();
    // 예약 생성 성공 시, status REQUESTED로 수정
    // 이메일 알람 전송 후, emailNotification true로 수정
    }
}
