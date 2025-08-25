package com.recareer.backend.session.dto;

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
public class SessionRequestDto {

  @Schema(description = "멘토 ID", example = "1")
  @NotNull(message = "멘토 ID는 필수입니다.")
  private Long mentorId;

  @Schema(description = "세션 시간", example = "2025-08-01T14:00:00")
  @Future(message = "세션 시간은 현재보다 이후여야 합니다.")
  @NotNull(message = "세션 시간은 필수입니다.")
  private LocalDateTime sessionTime;
}