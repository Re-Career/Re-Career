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

    public static ReservationListResponseDto from(Reservation reservation) {
        return ReservationListResponseDto.builder()
                .reservationId(reservation.getId())
                .reservationTime(reservation.getReservationTime())
                .status(reservation.getStatus())
                .cancelReason(reservation.getStatus() == ReservationStatus.CANCELED ? reservation.getCancelReason() : null)
                .mentor(MentorInfo.builder()
                        .mentorId(reservation.getMentor().getId())
                        .name(reservation.getMentor().getUser().getName())
                        .position(PositionDto.builder()
                                .id(reservation.getMentor().getPositionEntity().getId())
                                .name(reservation.getMentor().getPositionEntity().getName())
                                .build())
                        .profileImageUrl(reservation.getMentor().getUser().getProfileImageUrl())
                        .build())
                .build();
    }
}