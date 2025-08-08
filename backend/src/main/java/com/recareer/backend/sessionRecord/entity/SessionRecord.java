package com.recareer.backend.sessionRecord.entity;

import com.recareer.backend.common.entity.BaseTimeEntity;
import com.recareer.backend.reservation.entity.Reservation;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "session_records")
public class SessionRecord extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(optional = false)
  @JoinColumn(name = "reservation_id", nullable = false)
  private Reservation reservation;

  private String text;

  private String feedback;
}
