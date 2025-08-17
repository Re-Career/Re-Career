package com.recareer.backend.position.entity;

import com.recareer.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "position_responsibility_map")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionResponsibilityMap extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "responsibility_id", nullable = false)
    private PositionResponsibilities positionResponsibilities;
}