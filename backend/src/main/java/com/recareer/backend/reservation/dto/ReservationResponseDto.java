package com.recareer.backend.reservation.dto;

import com.recareer.backend.reservation.entity.Reservation;
import com.recareer.backend.reservation.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponseDto {

    // 예약 ID
    private Long reservationId;
    
    // 예약 시간
    private LocalDateTime reservationTime;
    
    // 예약 상태
    private Status status;
    
    // 이메일 알림 여부
    private boolean emailNotification;
    
    // 멘토 정보
    private MentorInfo mentor;
    
    // 유저 정보 
    private UserInfo user;
    
    // 생성/수정 시간
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MentorInfo {
        private Long mentorId;
        private String name;
        private String email;
        private String position;
        private String profileImageUrl;
        private Boolean isVerified;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long userId;
        private String name;
        private String email;
        private String profileImageUrl;
        private String region;
    }

    // Entity를 DTO로 변환하는 정적 메소드
    public static ReservationResponseDto from(Reservation reservation) {
        return ReservationResponseDto.builder()
                .reservationId(reservation.getId())
                .reservationTime(reservation.getReservationTime())
                .status(reservation.getStatus())
                .emailNotification(reservation.isEmailNotification())
                .mentor(MentorInfo.builder()
                        .mentorId(reservation.getMentor().getId())
                        .name(reservation.getMentor().getUser().getName())
                        .email(reservation.getMentor().getUser().getEmail())
                        .position(reservation.getMentor().getPosition())
                        .profileImageUrl(reservation.getMentor().getUser().getProfileImageUrl())
                        .isVerified(reservation.getMentor().getIsVerified())
                        .build())
                .user(UserInfo.builder()
                        .userId(reservation.getUser().getId())
                        .name(reservation.getUser().getName())
                        .email(reservation.getUser().getEmail())
                        .profileImageUrl(reservation.getUser().getProfileImageUrl())
                        .region(reservation.getUser().getRegion())
                        .build())
                .createdAt(reservation.getCreatedDate())
                .updatedAt(reservation.getModifiedDate())
                .build();
    }
}