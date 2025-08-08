package com.recareer.backend.availableTime.entity;

import com.recareer.backend.common.entity.BaseTimeEntity;
import com.recareer.backend.mentor.entity.Mentor;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "available_times", uniqueConstraints = @UniqueConstraint(columnNames = {"mentor_id", "available_time"}))
public class AvailableTime extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mentor_id", nullable = false)
  private Mentor mentor;

  @Column(name = "available_time", nullable = false)
  private LocalDateTime availableTime;

  @Column(name = "is_booked")
  private boolean isBooked;
}
