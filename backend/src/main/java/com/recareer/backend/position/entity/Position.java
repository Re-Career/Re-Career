package com.recareer.backend.position.entity;

import com.recareer.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "positions")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Position extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "trend_rank", nullable = true)
    private Integer trendRank;

    @Column(name = "image_url", length = 256)
    private String imageUrl;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "industry_trends", columnDefinition = "TEXT")
    private String industryTrends;
}