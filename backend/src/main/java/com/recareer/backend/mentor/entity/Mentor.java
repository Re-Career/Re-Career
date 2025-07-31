package com.recareer.backend.mentor.entity;

import com.recareer.backend.common.entity.BaseTimeEntity;
import com.recareer.backend.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mentors")
public class Mentor extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(columnDefinition = "TEXT")
  private String position;

  @Column(columnDefinition = "TEXT")
  private String description;
}
