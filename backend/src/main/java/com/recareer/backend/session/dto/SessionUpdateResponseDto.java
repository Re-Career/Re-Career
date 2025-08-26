package com.recareer.backend.session.dto;

import com.recareer.backend.session.entity.Session;
import com.recareer.backend.session.entity.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionUpdateResponseDto {
    
    private Long id;
    private SessionStatus status;
    
    public static SessionUpdateResponseDto from(Session session) {
        return SessionUpdateResponseDto.builder()
                .id(session.getId())
                .status(session.getStatus())
                .build();
    }
}