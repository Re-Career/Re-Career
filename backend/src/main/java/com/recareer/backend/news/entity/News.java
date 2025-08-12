package com.recareer.backend.news.entity;

import com.recareer.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "news")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class News extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 20)
    private String title;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "url", nullable = false, length = 256)
    private String url;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "image_url", length = 256)
    private String imageUrl;
}