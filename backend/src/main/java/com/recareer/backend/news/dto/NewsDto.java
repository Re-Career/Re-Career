package com.recareer.backend.news.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsDto {
    private Long id;
    private String title;
    private String description;
    private String url;
    private String category;
    private String imageUrl;
}