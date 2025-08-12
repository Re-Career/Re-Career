package com.recareer.backend.email.entity;

import com.recareer.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 이메일 전송 큐 엔티티
 * 
 * 비동기 이메일 전송을 위한 큐 시스템의 핀심 엔티티입니다.
 * 이메일 전송 요청을 데이터베이스에 저장하고,
 * 전송 상태를 추적하여 실패 시 재시도가 가능하도록 합니다.
 * 
 * 주요 상태:
 * - PENDING: 전송 대기
 * - SENDING: 전송 중
 * - SENT: 전송 완료
 * - FAILED: 전송 실패
 */
@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "email_queue")
public class EmailQueue extends BaseTimeEntity {

    /** 이메일 큐 고유 ID (기본키) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 수신자 이메일 주소 */
    @Column(nullable = false)
    private String recipientEmail;

    /** 수신자 이름 */
    @Column(nullable = false)
    private String recipientName;

    /** 이메일 제목 */
    @Column(nullable = false)
    private String subject;

    /** 이메일 내용 (HTML 형식) */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 이메일 전송 상태 (기본값: PENDING) */
    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EmailStatus status = EmailStatus.PENDING;

    /** 재시도 횟수 (기본값: 0) */
    @Builder.Default
    @Column(nullable = false)
    private Integer retryCount = 0;

    /** 마지막 전송 시도 시간 */
    @Column
    private LocalDateTime lastAttemptAt;

    /** 오류 메시지 (전송 실패 시 저장) */
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    /** 연관된 멘토링 예약 ID (추적 목적) */
    @Column
    private Long reservationId;

    /**
     * 재시도 횟수를 1 증가시키고 마지막 시도 시간을 현재 시각으로 업데이트합니다.
     */
    public void incrementRetryCount() {
        this.retryCount++;
        this.lastAttemptAt = LocalDateTime.now();
    }

    /**
     * 이메일을 전송 완료 상태로 표시합니다.
     * 상태를 SENT로 변경하고 마지막 시도 시간을 현재 시각으로 업데이트합니다.
     */
    public void markAsSent() {
        this.status = EmailStatus.SENT;
        this.lastAttemptAt = LocalDateTime.now();
    }

    /**
     * 이메일을 전송 실패 상태로 표시합니다.
     * 상태를 FAILED로 변경하고 오류 메시지를 저장합니다.
     * 
     * @param errorMessage 전송 실패 오류 메시지
     */
    public void markAsFailed(String errorMessage) {
        this.status = EmailStatus.FAILED;
        this.errorMessage = errorMessage;
        this.lastAttemptAt = LocalDateTime.now();
    }
}