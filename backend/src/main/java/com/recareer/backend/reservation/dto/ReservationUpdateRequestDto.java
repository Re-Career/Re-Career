package com.recareer.backend.reservation.dto;

import com.recareer.backend.reservation.entity.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationUpdateRequestDto {
    
    @NotNull(message = "상태는 필수입니다")
    private ReservationStatus status;
    
    private String cancelReason; // CANCELED일 때만 사용
}