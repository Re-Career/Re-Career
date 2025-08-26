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
public class SessionResponseDto {

    // 세션 ID
    private Long id;
    
    // 세션 시간
    private LocalDateTime sessionTime;
    
    // 세션 상태
    private SessionStatus status;
    
    // 멘토 정보
    private MentorInfo mentor;
    
    // 멘티 정보
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

    // Entity를 DTO로 변환하는 정적 메소드 - 멘티 관점용 (멘토 정보 포함)
    public static SessionResponseDto fromForMentee(Session session) {
        return SessionResponseDto.builder()
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

    // Entity를 DTO로 변환하는 정적 메소드 - 멘토 관점용 (멘티 정보 포함)
    public static SessionResponseDto fromForMentor(Session session) {
        return SessionResponseDto.builder()
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