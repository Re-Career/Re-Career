package com.recareer.backend.reservation.service;

import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.repository.MentorRepository;
import com.recareer.backend.reservation.dto.ReservationCancelRequestDto;
import com.recareer.backend.reservation.dto.ReservationRequestDto;
import com.recareer.backend.reservation.dto.ReservationResponseDto;
import com.recareer.backend.reservation.entity.Reservation;
import com.recareer.backend.reservation.entity.ReservationStatus;
import com.recareer.backend.reservation.repository.ReservationRepository;
import com.recareer.backend.user.entity.User;
import com.recareer.backend.user.repository.UserRepository;
import com.recareer.backend.mentoringRecord.entity.MentoringRecord;
import com.recareer.backend.mentoringRecord.entity.MentoringRecordStatus;
import com.recareer.backend.mentoringRecord.repository.MentoringRecordRepository;
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
  private final MentoringRecordRepository mentoringRecordRepository;

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
        .build();

    Reservation newReservation = reservationRepository.save(reservation);

    return newReservation.getId();
    // TODO: 멘토링 생성 성공 시, status REQUESTED로 수정
    // TODO: 멘토링 확정 시, email로 멘토링 알람 구현
    // TODO: 이메일 알람 전송 후, emailNotification true로 수정
    }

  @Override
  @Transactional(readOnly = true)
  public Reservation findById(Long reservationId) {
    return reservationRepository.findById(reservationId)
            .orElseThrow(() -> new EntityNotFoundException("해당 예약을 찾을 수 없습니다."));
  }
  
  
  private void createMentoringRecordIfNotExists(Reservation reservation) {
    // 이미 MentoringRecord가 존재하는지 확인
    boolean exists = mentoringRecordRepository.existsByReservationId(reservation.getId());
    
    if (!exists) {
      MentoringRecord mentoringRecord = MentoringRecord.builder()
          .reservation(reservation)
          .status(MentoringRecordStatus.AUDIO_PENDING)
          .build();
      
      mentoringRecordRepository.save(mentoringRecord);
    }
  }
  
  @Override
  @Transactional
  public void acceptReservation(Long reservationId) {
    Reservation reservation = findById(reservationId);
    
    // 비즈니스 검증: REQUESTED 상태에서만 수락 가능
    if (reservation.getStatus() != ReservationStatus.REQUESTED) {
      throw new IllegalStateException("요청된 상태의 예약만 수락할 수 있습니다.");
    }
    
    // 멘토링 수락 처리 및 상태 변경
    reservation.setStatus(ReservationStatus.CONFIRMED);

    reservationRepository.save(reservation);
    
    // TODO: 이메일 알림 발송 로직 추가 가능
  }
  
  @Override
  @Transactional
  public void completeReservation(Long reservationId) {
    Reservation reservation = findById(reservationId);
    
    // 비즈니스 검증: CONFIRMED 상태에서만 완료 가능
    if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
      throw new IllegalStateException("확인된 상태의 예약만 완료할 수 있습니다.");
    }
    
    // 멘토링 완료 처리 및 상태 변경
    reservation.setStatus(ReservationStatus.COMPLETED);

    reservationRepository.save(reservation);
    
    // 멘토링 완료 시 자동으로 MentoringRecord 생성
    createMentoringRecordIfNotExists(reservation);
  }
  
  @Override
  @Transactional
  public void cancelReservation(Long reservationId, ReservationCancelRequestDto cancelRequestDto) {
    Reservation reservation = findById(reservationId);
    
    // 비즈니스 검증: COMPLETED가 아닌 상태에서만 취소 가능
    if (reservation.getStatus() == ReservationStatus.COMPLETED) {
      throw new IllegalStateException("완료된 멘토링은 취소할 수 없습니다.");
    }
    
    // 멘토링 취소 처리 및 상태 변경
    reservation.setStatus(ReservationStatus.CANCELED);
    reservation.setCancelReason(cancelRequestDto.getCancelReason());

    reservationRepository.save(reservation);
    
    // TODO: 취소 알림 발송 로직 추가 가능
  }
}
