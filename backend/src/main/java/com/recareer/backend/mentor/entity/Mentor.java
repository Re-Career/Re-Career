package com.recareer.backend.mentor.entity;

import com.recareer.backend.career.entity.MentorCareer;
import com.recareer.backend.common.entity.BaseTimeEntity;
import com.recareer.backend.feedback.entity.MentorFeedback;
import com.recareer.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

  @ElementCollection
  @CollectionTable(name = "mentor_skills", joinColumns = @JoinColumn(name = "mentor_id"))
  @Column(name = "skill", length = 50)
  @Builder.Default
  private List<String> skills = new ArrayList<>();

  @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<MentorCareer> careers = new ArrayList<>();

  @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<MentorFeedback> feedbacks = new ArrayList<>();

  public Mentor update(String position, String description, Integer experience, MentoringType mentoringType) {
    this.position = position;
    this.description = description;
    this.experience = experience;
    this.mentoringType = mentoringType;
    return this;
  }

  public Mentor updateSkills(List<String> skills) {
    this.skills.clear();
    if (skills != null) {
      this.skills.addAll(skills);
    }
    return this;
  }
}
