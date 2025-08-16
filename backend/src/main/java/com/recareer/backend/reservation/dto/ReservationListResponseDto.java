package com.recareer.backend.reservation.dto;

import com.recareer.backend.reservation.entity.Reservation;
import com.recareer.backend.reservation.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationListResponseDto {

    private Long reservationId;
    private LocalDateTime reservationTime;
    private ReservationStatus status;
    private String cancelReason;
    private MentorInfo mentor;
    private UserInfo user;

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
    public static class UserInfo {
        private Long userId;
        private String name;
        private String profileImageUrl;
    }

    public static ReservationListResponseDto from(Reservation reservation) {
        return ReservationListResponseDto.builder()
                .reservationId(reservation.getId())
                .reservationTime(reservation.getReservationTime())
                .status(reservation.getStatus())
                .cancelReason(reservation.getStatus() == ReservationStatus.CANCELED ? reservation.getCancelReason() : null)
                .mentor(MentorInfo.builder()
                        .mentorId(reservation.getMentor().getId())
                        .name(reservation.getMentor().getUser().getName())
                        .position(reservation.getMentor().getPosition())
                        .profileImageUrl(reservation.getMentor().getUser().getProfileImageUrl())
                        .build())
                .user(UserInfo.builder()
                        .userId(reservation.getUser().getId())
                        .name(reservation.getUser().getName())
                        .profileImageUrl(reservation.getUser().getProfileImageUrl())
                        .build())
                .build();
    }
}