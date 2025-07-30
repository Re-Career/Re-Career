package com.recareer.backend.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequestDto {

  @Schema(description = "멘토 ID", example = "1")
  @NotNull(message = "멘토 ID는 필수입니다.")
  private Long mentorId;

  @Schema(description = "멘티(사용자) ID", example = "2")
  @NotNull(message = "멘티 ID는 필수입니다.")
  private Long userId;

  @Schema(description = "예약 시간", example = "2025-08-01T14:00:00")
  @Future(message = "예약 시간은 현재보다 이후여야 합니다.")
  @NotNull(message = "예약 시간은 필수입니다.")
  private LocalDateTime reservationTime;
}