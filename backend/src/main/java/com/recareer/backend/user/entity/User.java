package com.recareer.backend.user.entity;

import com.recareer.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @Column(unique = true)
  private String email;

  @Enumerated(EnumType.STRING)
  private Role role;

  @Column(name = "provider")
  private String provider;

  @Column(name = "provider_id", unique = true)
  private String providerId;

  @Column(name = "profile_image")
  private String profileImage;

  public User update(String name, String profileImage) {
    this.name = name;
    this.profileImage = profileImage;
    return this;
  }

  public String getRoleKey() {
    return this.role.getKey();
  }
}