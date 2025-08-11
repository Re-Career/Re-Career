package com.recareer.backend.position.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionResponsibilitiesDto {
    private Long id;
    private String name;
    private String imageUrl;
}