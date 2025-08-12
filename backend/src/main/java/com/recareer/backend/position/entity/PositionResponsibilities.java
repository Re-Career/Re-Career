package com.recareer.backend.position.entity;

import com.recareer.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "position_responsibilities")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionResponsibilities extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "image_url", length = 256)
    private String imageUrl;
}