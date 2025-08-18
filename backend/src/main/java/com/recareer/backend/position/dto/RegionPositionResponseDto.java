package com.recareer.backend.position.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionPositionResponseDto {
    private String province;
    private String city;
    private List<PositionSimpleDto> positions;
}