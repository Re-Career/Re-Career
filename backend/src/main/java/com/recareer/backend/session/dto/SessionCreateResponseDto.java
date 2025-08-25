package com.recareer.backend.session.dto;

import com.recareer.backend.session.entity.Session;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionCreateResponseDto {

    private Long id;
    private String mentorName;
    private String menteeName;
    private LocalDateTime sessionTime;

    public static SessionCreateResponseDto from(Session session) {
        return SessionCreateResponseDto.builder()
                .id(session.getId())
                .mentorName(session.getMentor().getUser().getName())
                .menteeName(session.getUser().getName())
                .sessionTime(session.getSessionTime())
                .build();
    }
}