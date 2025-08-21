package com.recareer.backend.reservation.service;

import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.repository.MentorRepository;
import com.recareer.backend.reservation.dto.ReservationCancelRequestDto;
import com.recareer.backend.reservation.dto.ReservationRequestDto;
import com.recareer.backend.reservation.dto.ReservationResponseDto;
import com.recareer.backend.reservation.dto.ReservationUpdateRequestDto;
import com.recareer.backend.reservation.entity.Reservation;
import com.recareer.backend.reservation.entity.ReservationStatus;
import com.recareer.backend.reservation.repository.ReservationRepository;
import com.recareer.backend.user.entity.User;
import com.recareer.backend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
  private final ReservationRepository reservationRepository;
  private final UserRepository userRepository;
  private final MentorRepository mentorRepository;

  @Override
  @Transactional(readOnly = true)
  public List<ReservationResponseDto> findAllReservationsByUserId(Long userId) {
    List<Reservation> reservations = reservationRepository.findAllByUserId(userId);
    return reservations.stream()
            .map(ReservationResponseDto::from)
            .toList();
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
        .status(ReservationStatus.REQUESTED)
        .build();

    Reservation newReservation = reservationRepository.save(reservation);

    return newReservation.getId();
    }

  @Override
  @Transactional(readOnly = true)
  public Reservation findById(Long reservationId) {
    return reservationRepository.findById(reservationId)
            .orElseThrow(() -> new EntityNotFoundException("해당 예약을 찾을 수 없습니다."));
  }
  

  @Override
  @Transactional
  public void updateReservationStatus(Long reservationId, ReservationUpdateRequestDto updateRequestDto) {
    Reservation reservation = findById(reservationId);
    ReservationStatus newStatus = updateRequestDto.getStatus();
    
    // 상태별 비즈니스 로직 검증 및 처리
    switch (newStatus) {
      case CONFIRMED -> {
        if (reservation.getStatus() != ReservationStatus.REQUESTED) {
          throw new IllegalStateException("요청된 상태의 예약만 수락할 수 있습니다.");
        }
        reservation.setStatus(ReservationStatus.CONFIRMED);
      }
      case COMPLETED -> {
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
          throw new IllegalStateException("확인된 상태의 예약만 완료할 수 있습니다.");
        }
        reservation.setStatus(ReservationStatus.COMPLETED);
      }
      case CANCELED -> {
        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
          throw new IllegalStateException("완료된 멘토링은 취소할 수 없습니다.");
        }
        if (updateRequestDto.getCancelReason() == null || updateRequestDto.getCancelReason().trim().isEmpty()) {
          throw new IllegalArgumentException("취소 사유는 필수입니다.");
        }
        reservation.setStatus(ReservationStatus.CANCELED);
        reservation.setCancelReason(updateRequestDto.getCancelReason());
      }
      default -> throw new IllegalArgumentException("지원하지 않는 상태입니다: " + newStatus);
    }
    
    reservationRepository.save(reservation);
  }

  @Override
  @Transactional
  public void cancelReservation(Long reservationId, ReservationCancelRequestDto cancelRequestDto) {
    Reservation reservation = findById(reservationId);
    
    // 완료된 멘토링은 취소할 수 없음
    if (reservation.getStatus() == ReservationStatus.COMPLETED) {
      throw new IllegalStateException("완료된 멘토링은 취소할 수 없습니다.");
    }
    
    // 이미 취소된 멘토링은 중복 취소할 수 없음
    if (reservation.getStatus() == ReservationStatus.CANCELED) {
      throw new IllegalStateException("이미 취소된 멘토링입니다.");
    }
    
    // 취소 사유 검증
    if (cancelRequestDto.getCancelReason() == null || cancelRequestDto.getCancelReason().trim().isEmpty()) {
      throw new IllegalArgumentException("취소 사유는 필수입니다.");
    }
    
    // 상태 변경
    reservation.setStatus(ReservationStatus.CANCELED);
    reservation.setCancelReason(cancelRequestDto.getCancelReason());
    
    reservationRepository.save(reservation);
    
    log.info("예약 취소 완료 - 예약 ID: {}, 취소 사유: {}", reservationId, cancelRequestDto.getCancelReason());
  }
}
