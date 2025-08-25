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
public class SessionDetailResponseDto {
    
    private Long id;
    private LocalDateTime sessionTime;
    private SessionStatus status;
    
    // 멘토 정보 (MENTEE 역할일 때 사용)
    private MentorInfo mentor;
    
    // 멘티 정보 (MENTOR 역할일 때 사용)
    private MenteeInfo mentee;
    
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
    public static class MenteeInfo {
        private Long menteeId;
        private String name;
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
    
    // MENTEE 역할용 (멘토 정보 포함)
    public static SessionDetailResponseDto fromForMentee(Session session) {
        return SessionDetailResponseDto.builder()
                .id(session.getId())
                .sessionTime(session.getSessionTime())
                .status(session.getStatus())
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
    
    // MENTOR 역할용 (멘티 정보 포함)
    public static SessionDetailResponseDto fromForMentor(Session session) {
        return SessionDetailResponseDto.builder()
                .id(session.getId())
                .sessionTime(session.getSessionTime())
                .status(session.getStatus())
                .mentee(MenteeInfo.builder()
                        .menteeId(session.getUser().getId())
                        .name(session.getUser().getName())
                        .profileImageUrl(session.getUser().getProfileImageUrl())
                        .build())
                .build();
    }
}