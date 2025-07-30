package com.recareer.backend.reservation.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationRequestDto {

  @NotNull(message = "멘토 ID는 필수입니다.")
  private Long mentorId;

  @NotNull(message = "멘티 ID는 필수입니다.")
  private Long userId;

  @NotNull(message = "예약 시간은 필수입니다.")
  @Future(message = "예약 시간은 미래여야 합니다.")
  private LocalDateTime reservationTime;

  private boolean emailNotification;
}
