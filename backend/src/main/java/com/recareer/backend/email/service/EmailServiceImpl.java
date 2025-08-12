package com.recareer.backend.email.service;

import com.recareer.backend.email.entity.EmailQueue;
import com.recareer.backend.email.entity.EmailStatus;
import com.recareer.backend.email.repository.EmailQueueRepository;
import com.recareer.backend.reservation.entity.Reservation;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

/**
 * 이메일 전송 서비스 구현체
 * 
 * 멘토링 확정 알림 등의 이메일을 비동기적으로 전송합니다.
 * 전송 실패 시 큐 시스템을 통해 자동 재시도를 수행합니다.
 * 
 * 주요 기능:
 * - 비동기 이메일 전송
 * - 실패 시 자동 재시도 (최대 3회)
 * - 중복 전송 방지
 * - 이메일 전송 상태 추적
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    
    /** 이메일 전송을 위한 JavaMailSender */
    private final JavaMailSender mailSender;
    
    /** 이메일 큐 관리를 위한 Repository */
    private final EmailQueueRepository emailQueueRepository;
    
    /** 최대 재시도 횟수 */
    private static final int MAX_RETRY_COUNT = 3;
    
    /**
     * 멘토링 확정 알림 이메일 전송
     * 
     * 멘토가 멘토링 요청을 승인했을 때 멘티에게 확정 알림 이메일을 전송합니다.
     * 이메일은 큐에 저장된 후 비동기적으로 처리됩니다.
     * 
     * @param reservation 확정된 멘토링 예약 정보
     * @throws RuntimeException 이메일 큐잉 실패 시
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendMentoringConfirmationEmail(Reservation reservation) {
        // 중복 전송 방지: 이미 전송된 이메일인지 확인
        if (emailQueueRepository.existsByReservationIdAndStatus(reservation.getId(), EmailStatus.SENT)) {
            log.info("이미 전송된 멘토링 확정 이메일: 예약 ID={}", reservation.getId());
            return;
        }
        
        // 이메일 전송에 필요한 정보 추출
        String menteeEmail = reservation.getUser().getEmail();  // 수신자 (멘티) 이메일
        String menteeName = reservation.getUser().getName();    // 멘티 이름
        String mentorName = reservation.getMentor().getUser().getName(); // 멘토 이름
        String mentoringDate = reservation.getReservationTime()  // 멘토링 예정 일시
            .format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm"));
        
        // 이메일 제목 및 내용 생성
        String subject = "[Re-Career] 멘토링이 확정되었습니다";
        String content = createEmailContent(menteeName, mentorName, mentoringDate);
        
        // 이메일을 큐에 저장
        EmailQueue emailQueue = EmailQueue.builder()
            .recipientEmail(menteeEmail)     // 수신자 이메일
            .recipientName(menteeName)       // 수신자 이름
            .subject(subject)               // 이메일 제목
            .content(content)               // 이메일 내용
            .reservationId(reservation.getId()) // 연관된 예약 ID
            .build();
        
        emailQueueRepository.save(emailQueue);
        
        // 비동기적으로 이메일 전송 시작
        sendEmailAsync(emailQueue.getId());
    }
    
    /**
     * 비동기 이메일 전송 처리
     * 
     * 큐에 저장된 이메일을 실제로 전송합니다.
     * 실패 시 Spring Retry를 통해 자동 재시도됩니다.
     * 
     * @param emailQueueId 전송할 이메일 큐의 ID
     * @throws RuntimeException 이메일 전송 실패 시
     */
    @Async
    @Transactional
    @Retryable(retryFor = {Exception.class}, maxAttempts = MAX_RETRY_COUNT, backoff = @Backoff(delay = 5000, multiplier = 2))
    public void sendEmailAsync(Long emailQueueId) {
        // 이메일 큐에서 전송할 이메일 정보 조회
        EmailQueue emailQueue = emailQueueRepository.findById(emailQueueId)
            .orElseThrow(() -> new IllegalArgumentException("이메일 큐를 찾을 수 없습니다: " + emailQueueId));
        
        // 이미 전송된 이메일인지 확인
        if (emailQueue.getStatus() == EmailStatus.SENT) {
            return;
        }
        
        try {
            // 이메일 상태를 '전송 중'으로 변경하고 재시도 횟수 증가
            emailQueue.setStatus(EmailStatus.SENDING);
            emailQueue.incrementRetryCount();
            emailQueueRepository.save(emailQueue);
            
            // HTML 이메일 메시지 생성
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            // 이메일 헤더 설정
            helper.setTo(emailQueue.getRecipientEmail());  // 수신자
            helper.setSubject(emailQueue.getSubject());    // 제목
            helper.setText(emailQueue.getContent(), true); // HTML 내용
            
            // 실제 이메일 전송
            mailSender.send(message);
            
            // 전송 성공 시 상태 업데이트
            emailQueue.markAsSent();
            emailQueueRepository.save(emailQueue);
            
            log.info("이메일 전송 완료: 수신자={}, 제목={}", emailQueue.getRecipientEmail(), emailQueue.getSubject());
            
        } catch (Exception e) {
            log.error("이메일 전송 실패 (시도 {}/{}): 수신자={}, 오류={}", 
                     emailQueue.getRetryCount(), MAX_RETRY_COUNT, emailQueue.getRecipientEmail(), e.getMessage());
            
            // 최대 재시도 횟수에 도달했는지 확인
            if (emailQueue.getRetryCount() >= MAX_RETRY_COUNT) {
                // 최대 재시도 횟수 초과 시 실패로 표시
                emailQueue.markAsFailed(e.getMessage());
                emailQueueRepository.save(emailQueue);
                log.error("이메일 전송 최종 실패: 수신자={}, 최대 재시도 횟수 초과", emailQueue.getRecipientEmail());
            } else {
                // 아직 재시도 가능한 경우 대기 상태로 변경 (스케줄러가 재시도)
                emailQueue.setStatus(EmailStatus.PENDING);
                emailQueueRepository.save(emailQueue);
            }
            
            // Spring Retry가 재시도할 수 있도록 예외를 다시 던짐
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }
    
    /**
     * 멘토링 확정 이메일의 HTML 내용을 생성합니다.
     * 
     * @param menteeName 멘티 이름
     * @param mentorName 멘토 이름
     * @param mentoringDate 멘토링 예정 일시
     * @return HTML 형식의 이메일 내용
     */
    private String createEmailContent(String menteeName, String mentorName, String mentoringDate) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;">
                    <h2 style="color: #2c5aa0; text-align: center;">멘토링이 성공적으로 확정되었습니다!</h2>
                    
                    <div style="background-color: #f8f9fa; padding: 20px; border-radius: 5px; margin: 20px 0;">
                        <h3 style="margin-top: 0; color: #495057;">멘토링 정보</h3>
                        <p><strong>멘티:</strong> %s</p>
                        <p><strong>멘토:</strong> %s</p>
                        <p><strong>멘토링 날짜:</strong> %s</p>
                        <p><strong>진행 방식:</strong> 온라인</p>
                    </div>
                    
                    <div style="text-align: center; margin: 30px 0;">
                        <p style="font-size: 16px;">예정된 시간에 멘토링이 진행될 예정입니다.</p>
                        <p style="color: #6c757d;">멘토링 시작 전 미리 준비해주세요!</p>
                    </div>
                    
                    <hr style="border: none; border-top: 1px solid #eee; margin: 30px 0;">
                    
                    <div style="text-align: center; color: #6c757d; font-size: 14px;">
                        <p>Re-Career 팀 드림</p>
                        <p>이 메일에 대한 문의사항이 있으시면 고객센터로 연락해주세요.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(menteeName, mentorName, mentoringDate);
    }
}