package com.recareer.backend.mentor.entity;

import com.recareer.backend.common.entity.BaseTimeEntity;
import com.recareer.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mentors")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mentor extends BaseTimeEntity {

  @Id
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(length = 100)
  private String position;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Builder.Default
  @Column(name = "is_verified", nullable = false)
  private Boolean isVerified = false;

  @Column(name = "experience")
  private Integer experience;

  @Enumerated(EnumType.STRING)
  @Column(name = "mentoring_type")
  private MentoringType mentoringType;

  public Mentor update(String position, String description, Integer experience, MentoringType mentoringType) {
    this.position = position;
    this.description = description;
    this.experience = experience;
    this.mentoringType = mentoringType;
    return this;
  }
}
