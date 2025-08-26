package com.recareer.backend.session.dto;

import com.recareer.backend.session.entity.Session;
import com.recareer.backend.session.entity.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionListResponseDto {

    private Long id;
    private LocalDateTime sessionTime;
    private SessionStatus status;
    private String cancelReason;
    private MentorInfo mentor;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MentorInfo {
        private Long mentorId;
        private String name;
        private PositionDto position;
        private String profileImageUrl;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PositionDto {
        private Long id;
        private String name;
    }

    public static SessionListResponseDto from(Session session) {
        return SessionListResponseDto.builder()
                .id(session.getId())
                .sessionTime(session.getSessionTime())
                .status(session.getStatus())
                .cancelReason(session.getStatus() == SessionStatus.CANCELED ? session.getCancelReason() : null)
                .mentor(MentorInfo.builder()
                        .mentorId(session.getMentor().getId())
                        .name(session.getMentor().getUser().getName())
                        .position(PositionDto.builder()
                                .id(session.getMentor().getPositionEntity().getId())
                                .name(session.getMentor().getPositionEntity().getName())
                                .build())
                        .profileImageUrl(session.getMentor().getUser().getProfileImageUrl())
                        .build())
                .build();
    }
}