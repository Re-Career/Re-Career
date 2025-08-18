package com.recareer.backend.position.entity;

import com.recareer.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "position_responsibility_map",
       uniqueConstraints = {
           @UniqueConstraint(
               name = "uk_position_responsibility_map",
               columnNames = {"position_id", "responsibility_id"}
           )
       })
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionResponsibilityMap extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsibility_id", nullable = false)
    private PositionResponsibilities positionResponsibilities;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        PositionResponsibilityMap that = (PositionResponsibilityMap) obj;
        return Objects.equals(position != null ? position.getId() : null,
                             that.position != null ? that.position.getId() : null) &&
               Objects.equals(positionResponsibilities != null ? positionResponsibilities.getId() : null,
                             that.positionResponsibilities != null ? that.positionResponsibilities.getId() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            position != null ? position.getId() : null,
            positionResponsibilities != null ? positionResponsibilities.getId() : null
        );
    }
}