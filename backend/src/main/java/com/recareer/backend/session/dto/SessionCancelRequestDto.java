package com.recareer.backend.session.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionCancelRequestDto {

  @Schema(description = "취소 사유", example = "개인 사정으로 인한 취소")
  @NotBlank(message = "취소 사유는 필수입니다.")
  private String cancelReason;
}