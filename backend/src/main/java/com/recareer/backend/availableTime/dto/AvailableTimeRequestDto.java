
package com.recareer.backend.availableTime.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class AvailableTimeRequestDto {
    
    @NotNull(message = "가능한 시간은 필수입니다")
    private LocalDateTime availableTime;
}