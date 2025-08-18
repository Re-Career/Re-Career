package com.recareer.backend.mentor.entity;

import com.recareer.backend.career.entity.MentorCareer;
import com.recareer.backend.common.entity.BaseTimeEntity;
import com.recareer.backend.common.entity.Company;
import com.recareer.backend.common.entity.Job;
import com.recareer.backend.feedback.entity.MentorFeedback;
import com.recareer.backend.skill.entity.MentorSkill;
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

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_id")
  private Job job;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id")
  private Company company;

  @Column(columnDefinition = "TEXT")
  private String description; // 간단한 소개 (shortDescription)

  @Column(columnDefinition = "TEXT")
  private String introduction; // 상세 소개

  @Builder.Default
  @Column(name = "is_verified", nullable = false)
  private Boolean isVerified = true; // TODO: 건강보험 등록증 확인을 통한 멘토 인증 로직 구현 필요

  @Column(name = "experience")
  private Integer experience;


  @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<MentorSkill> mentorSkills = new ArrayList<>();

  @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<MentorCareer> careers = new ArrayList<>();

  @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<MentorFeedback> feedbacks = new ArrayList<>();

  public Mentor update(Job job, Company company, String description, String introduction, Integer experience) {
    this.job = job;
    this.company = company;
    this.description = description;
    this.introduction = introduction;
    this.experience = experience;
    return this;
  }


  // 호환성을 위한 메서드들
  public String getPosition() {
    return job != null ? job.getName() : null;
  }

  public String getCompanyName() {
    return company != null ? company.getName() : null;
  }

  public String getProvinceName() {
    return user != null && user.getProvince() != null ? user.getProvince().getName() : null;
  }

  public String getCityName() {
    return user != null && user.getCity() != null ? user.getCity().getName() : null;
  }
}
