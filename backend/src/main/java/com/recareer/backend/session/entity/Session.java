package com.recareer.backend.session.entity;

import com.recareer.backend.common.entity.BaseTimeEntity;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sessions")
public class Session extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "mentor_id", nullable = false)
  private Mentor mentor;

  @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false, name = "session_time")
  private LocalDateTime sessionTime;


  @Builder.Default
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private SessionStatus status = SessionStatus.REQUESTED;

  // 취소 사유 (취소 시에만 사용)
  @Column(name = "cancel_reason", columnDefinition = "TEXT")
  private String cancelReason;

  /**
   * 주어진 사용자 ID가 해당 세션의 멘토인지 확인합니다.
   * 
   * @param userId 확인할 사용자 ID
   * @return 해당 사용자가 멘토이면 true, 아니면 false
   */
  public boolean isMentorParticipant(Long userId) {
    return mentor != null && mentor.getUser() != null && mentor.getUser().getId().equals(userId);
  }

  /**
   * 주어진 사용자 ID가 해당 세션의 멘티인지 확인합니다.
   * 
   * @param userId 확인할 사용자 ID
   * @return 해당 사용자가 멘티이면 true, 아니면 false
   */
  public boolean isMenteeParticipant(Long userId) {
    return user != null && user.getId().equals(userId);
  }

}