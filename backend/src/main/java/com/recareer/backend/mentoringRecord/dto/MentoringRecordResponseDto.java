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
public class MentoringRecordResponseDto {

    // 멘토링 기록 ID
    private Long mentoringRecordId;
    
    // 예약 정보
    private ReservationInfo reservation;
    
    // 멘토링 후속 처리 상태
    private MentoringRecordStatus status;
    
    // 멘티 피드백 (있는 경우만)
    private String menteeFeedback;
    
    // AI 요약 (있는 경우만)  
    private String summary;
    
    // 오디오 파일 존재 여부
    private boolean hasAudioFile;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservationInfo {
        private Long reservationId;
        private LocalDateTime reservationTime;
        private MentorInfo mentor;
        private MenteeInfo mentee;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MentorInfo {
        private Long mentorId;
        private String name;
        private String position;
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

    // Entity를 DTO로 변환하는 정적 메소드
    public static MentoringRecordResponseDto from(MentoringRecord mentoringRecord) {
        return MentoringRecordResponseDto.builder()
                .mentoringRecordId(mentoringRecord.getId())
                .reservation(ReservationInfo.builder()
                        .reservationId(mentoringRecord.getReservation().getId())
                        .reservationTime(mentoringRecord.getReservation().getReservationTime())
                        .mentor(MentorInfo.builder()
                                .mentorId(mentoringRecord.getReservation().getMentor().getId())
                                .name(mentoringRecord.getReservation().getMentor().getUser().getName())
                                .position(mentoringRecord.getReservation().getMentor().getPosition())
                                .profileImageUrl(mentoringRecord.getReservation().getMentor().getUser().getProfileImageUrl())
                                .build())
                        .mentee(MenteeInfo.builder()
                                .menteeId(mentoringRecord.getReservation().getUser().getId())
                                .name(mentoringRecord.getReservation().getUser().getName())
                                .profileImageUrl(mentoringRecord.getReservation().getUser().getProfileImageUrl())
                                .build())
                        .build())
                .status(mentoringRecord.getStatus())
                .menteeFeedback(mentoringRecord.getMenteeFeedback())
                .summary(mentoringRecord.getSummary())
                .hasAudioFile(mentoringRecord.getAudioFileUrl() != null && !mentoringRecord.getAudioFileUrl().trim().isEmpty())
                .build();
    }
}