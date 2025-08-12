package com.recareer.backend.position.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionSimpleDto {
    private Long id;
    private String name;
    private String imageUrl;
    private String category;
}