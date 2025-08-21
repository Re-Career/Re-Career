package com.recareer.backend.mentoringRecord.dto;

import com.recareer.backend.mentoringRecord.entity.MentoringRecord;
import com.recareer.backend.mentoringRecord.entity.MentoringRecordStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentoringRecordListResponseDto {

    private Long mentoringRecordId;
    private LocalDateTime reservationTime;
    private MentoringRecordStatus status;
    private String mentorName;
    private String mentorPosition;
    private String menteeName;
    private boolean hasAudioFile;
    private boolean hasFeedback;

    public static MentoringRecordListResponseDto from(MentoringRecord mentoringRecord) {        
        return MentoringRecordListResponseDto.builder()
                .mentoringRecordId(mentoringRecord.getId())
                .reservationTime(mentoringRecord.getSession().getSessionTime())
                .status(mentoringRecord.getStatus())
                .mentorName(mentoringRecord.getSession().getMentor().getUser().getName())
                .mentorPosition(mentoringRecord.getSession().getMentor().getPosition())
                .menteeName(mentoringRecord.getSession().getUser().getName())
                .hasAudioFile(mentoringRecord.getAudioFileUrl() != null && !mentoringRecord.getAudioFileUrl().trim().isEmpty())
                .hasFeedback(mentoringRecord.getMenteeFeedback() != null && !mentoringRecord.getMenteeFeedback().trim().isEmpty())
                .build();
    }
}