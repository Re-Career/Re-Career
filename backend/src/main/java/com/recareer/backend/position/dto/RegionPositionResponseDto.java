package com.recareer.backend.position.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionPositionResponseDto {
    private String region;
    private List<PositionSimpleDto> positions;
}