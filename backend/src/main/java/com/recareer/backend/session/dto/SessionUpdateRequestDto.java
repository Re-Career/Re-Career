package com.recareer.backend.session.dto;

import com.recareer.backend.session.entity.SessionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionUpdateRequestDto {
    
    @NotNull(message = "상태는 필수입니다")
    private SessionStatus status;
    
    private String cancelReason; // CANCELED일 때만 사용
}