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
public class ReservationResponseDto {

    // 예약 ID
    private Long reservationId;
    
    // 예약 시간
    private LocalDateTime reservationTime;
    
    // 예약 상태
    private ReservationStatus status;
    
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
    public static ReservationResponseDto from(Reservation reservation) {
        return ReservationResponseDto.builder()
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