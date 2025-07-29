package com.recareer.backend.reservation.entity;

import com.recareer.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reservation extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

//  @ManyToOne(optional = false)
//  @JoinColumn(name = "mentor_id", nullable = false)
//  private Mentor mentor;

//  @ManyToOne(optional = false)
//  @JoinColumn(name = "user_id", nullable = false)
//  private User user;

  @Column(nullable = false, name = "reservation_time")
  private LocalDateTime reservationTime;

  @Column(nullable = false, name = "email_notification")
  private boolean emailNotification = false;

  @Builder.Default
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Status status = Status.REQUESTED;
}