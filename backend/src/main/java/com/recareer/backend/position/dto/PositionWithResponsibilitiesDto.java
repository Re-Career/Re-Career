package com.recareer.backend.position.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionWithResponsibilitiesDto {
    private Long id;
    private String name;
    private String imageUrl;
    private String category;
    private String description;
    private String industryTrends;
    private List<PositionResponsibilitiesDto> responsibilities;
}