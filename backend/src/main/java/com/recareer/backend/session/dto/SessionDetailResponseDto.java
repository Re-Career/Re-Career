package com.recareer.backend.session.dto;

import com.recareer.backend.session.entity.Session;
import com.recareer.backend.session.entity.SessionStatus;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class SessionDetailResponseDto {
    
    private Long id;
    private String mentorName;
    private String menteeName;
    private LocalDateTime sessionTime;
    private SessionStatus status;
    private String menteeFeedback;
    private String audioFileUrl;
    private String transcribedText;
    private String summary;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    
    public static SessionDetailResponseDto from(Session session) {
        return SessionDetailResponseDto.builder()
                .id(session.getId())
                .mentorName(session.getMentor().getUser().getName())
                .menteeName(session.getUser().getName())
                .sessionTime(session.getSessionTime())
                .status(session.getStatus())
                .menteeFeedback(session.getMenteeFeedback())
                .audioFileUrl(session.getAudioFileUrl())
                .transcribedText(session.getTranscribedText())
                .summary(session.getSummary())
                .createdDate(session.getCreatedDate())
                .modifiedDate(session.getModifiedDate())
                .build();
    }
}