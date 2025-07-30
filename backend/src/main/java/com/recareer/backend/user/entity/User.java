package com.recareer.backend.user.entity;

import com.recareer.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @Column(unique = true)
  private String email;

  @Enumerated(EnumType.STRING)
  private Role role; // MENTOR, MENTEE

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}