package com.recareer.backend.availableTime.dto;

import com.recareer.backend.availableTime.entity.AvailableTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailableTimeResponseDto {

    private LocalDateTime availableTime;
    private boolean book;

    public static AvailableTimeResponseDto from(AvailableTime availableTime) {
        return AvailableTimeResponseDto.builder()
                .availableTime(availableTime.getAvailableTime())
                .book(availableTime.isBooked())
                .build();
    }
}