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
    private Long sessionId;
    
    // 세션 시간
    private LocalDateTime sessionTime;
    
    // 세션 상태
    private SessionStatus status;
    
    // 취소 사유 (취소된 경우에만)
    private String cancelReason;
    
    // 멘토 정보
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

    // Entity를 DTO로 변환하는 정적 메소드
    public static SessionResponseDto from(Session session) {
        return SessionResponseDto.builder()
                .sessionId(session.getId())
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