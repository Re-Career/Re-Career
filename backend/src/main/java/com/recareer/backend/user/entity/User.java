package com.recareer.backend.user.entity;

import com.recareer.backend.common.entity.BaseTimeEntity;
import com.recareer.backend.common.entity.City;
import com.recareer.backend.common.entity.Province;
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

  @Column(name = "profile_image_url")
  private String profileImageUrl;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "province_id", nullable = true)
  private Province province;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "city_id", nullable = true)
  private City city;

  public User update(String name, String profileImageUrl) {
    this.name = name;
    this.profileImageUrl = profileImageUrl;
    return this;
  }

  public User updateProfile(String name, String email, String profileImageUrl) {
    this.name = name;
    this.email = email;
    this.profileImageUrl = profileImageUrl;
    return this;
  }

  public User updateProfile(String name, String email, String profileImageUrl, Province province, City city) {
    this.name = name;
    this.email = email;
    this.profileImageUrl = profileImageUrl;
    this.province = province;
    this.city = city;
    return this;
  }

  public String getProvinceName() {
    return province != null ? province.getName() : null;
  }

  public String getCityName() {
    return city != null ? city.getName() : null;
  }

  public String getRoleKey() {
    return this.role.getKey();
  }
}