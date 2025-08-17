package com.recareer.backend.career.entity;

import com.recareer.backend.common.entity.BaseTimeEntity;
import com.recareer.backend.mentor.entity.Mentor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "mentor_careers")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorCareer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    @Column(nullable = false, length = 100)
    private String company; // 회사명

    @Column(nullable = false, length = 100)
    private String position; // 직책/포지션

    @Column(columnDefinition = "TEXT")
    private String description; // 업무 설명

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate; // 시작일

    @Column(name = "end_date")
    private LocalDate endDate; // 종료일 (null이면 현재 재직중)

    @Builder.Default
    @Column(name = "is_current", nullable = false)
    private Boolean isCurrent = false; // 현재 재직중 여부

    @Column(name = "display_order")
    private Integer displayOrder; // 표시 순서 (최신순으로 정렬하기 위함)

    public void updateCareer(String company, String position, String description, 
                           LocalDate startDate, LocalDate endDate, Boolean isCurrent, Integer displayOrder) {
        this.company = company;
        this.position = position;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isCurrent = isCurrent;
        this.displayOrder = displayOrder;
    }
}