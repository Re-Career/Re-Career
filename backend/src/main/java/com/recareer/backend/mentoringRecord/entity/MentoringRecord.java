package com.recareer.backend.mentoringRecord.entity;

import com.recareer.backend.common.entity.BaseTimeEntity;
import com.recareer.backend.reservation.entity.Reservation;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mentoring_records")
public class MentoringRecord extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(optional = false)
  @JoinColumn(name = "reservation_id", nullable = false, unique = true)
  private Reservation reservation;

  // 멘티가 상담 후 남기는 피드백
  @Column(name = "mentee_feedback", columnDefinition = "TEXT")
  private String menteeFeedback;

  // S3에 저장된 상담 녹음 파일 URL
  @Column(name = "audio_file_url")
  private String audioFileUrl;

  // 음성 인식 AI로 전사된 전체 상담 내용
  @Column(name = "transcribed_text", columnDefinition = "TEXT")
  private String transcribedText;

  // AI가 상담 내용을 분석하여 생성한 요약
  @Column(name = "summary", columnDefinition = "TEXT")
  private String summary;

  // 멘토링 후속 처리 상태
  @Builder.Default
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private MentoringRecordStatus status = MentoringRecordStatus.RECORD_PENDING;
}