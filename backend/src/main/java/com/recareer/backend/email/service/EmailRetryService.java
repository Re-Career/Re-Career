package com.recareer.backend.email.service;

import com.recareer.backend.email.entity.EmailQueue;
import com.recareer.backend.email.entity.EmailStatus;
import com.recareer.backend.email.repository.EmailQueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 이메일 재시도 서비스
 * 
 * 실패한 이메일들을 주기적으로 재시도하고 오래된 데이터를 정리합니다.
 * 
 * 주요 기능:
 * - 대기 중인 이메일 전송 처리
 * - 실패한 이메일 재시도
 * - 오래된 실패 이메일 자동 정리
 * - 배치 단위 처리로 성능 최적화
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailRetryService {
    
    /** 이메일 큐 데이터 액세스를 위한 Repository */
    private final EmailQueueRepository emailQueueRepository;
    
    /** 실제 이메일 전송을 수행하는 서비스 */
    private final EmailServiceImpl emailService;
    
    /** 한 번에 처리할 이메일 개수 (성능 고려) */
    private static final int BATCH_SIZE = 10;
    
    /** 최대 재시도 횟수 */
    private static final int MAX_RETRY_COUNT = 3;
    
    /** 재시도 전 대기 시간 (분) */
    private static final int RETRY_DELAY_MINUTES = 5;
    
    /**
     * 실패한 이메일 재시도 스케줄러
     * 
     * 1분마다 실행되어 다음 작업을 수행합니다:
     * 1. PENDING 상태의 이메일들을 전송 시도
     * 2. FAILED 상태이지만 재시도 가능한 이메일들을 다시 시도
     * 
     * 배치 단위로 처리하여 성능을 최적화하고,
     * 오류가 발생해도 다음 배치에 영향을 주지 않도록 처리합니다.
     */
    @Scheduled(fixedDelay = 60000) // 60초(1분)마다 실행
    @Transactional
    public void retryFailedEmails() {
        // 재시도 대상 시간 계산 (현재 시간 - 5분)
        LocalDateTime retryAfter = LocalDateTime.now().minusMinutes(RETRY_DELAY_MINUTES);
        
        // 1. PENDING 상태의 이메일들을 생성 순서대로 조회 (배치 크기만큼)
        List<EmailQueue> pendingEmails = emailQueueRepository.findByStatusOrderByCreatedDateAsc(
            EmailStatus.PENDING, PageRequest.of(0, BATCH_SIZE)
        );
        
        // 2. FAILED 상태이지만 재시도 가능한 이메일들을 조회
        // 조건: 최대 재시도 횟수 미달성 + 마지막 시도가 5분 이전
        List<EmailQueue> retryableEmails = emailQueueRepository.findRetryableEmails(
            EmailStatus.FAILED, MAX_RETRY_COUNT, retryAfter, PageRequest.of(0, BATCH_SIZE)
        );
        
        // 처리할 이메일이 있을 때만 로그 출력
        int totalProcessed = pendingEmails.size() + retryableEmails.size();
        if (totalProcessed > 0) {
            log.info("이메일 재시도 배치 시작: 대기={}, 재시도={}", pendingEmails.size(), retryableEmails.size());
        }
        
        // 3. PENDING 상태 이메일들 처리
        for (EmailQueue email : pendingEmails) {
            try {
                // 비동기적으로 이메일 전송 시도
                emailService.sendEmailAsync(email.getId());
            } catch (Exception e) {
                // 개별 이메일 전송 실패에도 다음 이메일 처리를 계속
                log.error("대기 중인 이메일 전송 실패: ID={}, 오류={}", email.getId(), e.getMessage());
            }
        }
        
        // 4. FAILED 상태이지만 재시도 가능한 이메일들 처리
        for (EmailQueue email : retryableEmails) {
            try {
                // 상태를 PENDING으로 변경하여 다시 시도할 수 있도록 설정
                email.setStatus(EmailStatus.PENDING);
                emailQueueRepository.save(email);
                
                // 비동기적으로 이메일 재전송 시도
                emailService.sendEmailAsync(email.getId());
            } catch (Exception e) {
                // 개별 이메일 재시도 실패에도 다음 이메일 처리를 계속
                log.error("재시도 이메일 전송 실패: ID={}, 오류={}", email.getId(), e.getMessage());
            }
        }
        
        // 처리 결과 로그 출력
        if (totalProcessed > 0) {
            log.info("이메일 재시도 배치 완료: 처리된 건수={}", totalProcessed);
        }
    }
    
    /**
     * 오래된 실패 이메일 정리 스케줄러
     * 
     * 매일 오전 1시에 실행되어 7일 이상 된 실패 이메일들을 자동으로 삭제합니다.
     * 
     * 목적:
     * - 데이터베이스 용량 관리
     * - 더 이상 재시도하지 않을 오래된 실패 데이터 제거
     * - 성능 최적화
     */
    @Scheduled(cron = "0 0 1 * * ?") // 매일 오전 1시에 실행
    @Transactional
    public void cleanupOldFailedEmails() {
        // 7일 전 시각을 기준으로 설정
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);
        
        // 7일 이상 된 실패 이메일들을 찾기
        // 조건: FAILED 상태 + 마지막 시도 시간이 7일 전보다 이전
        List<EmailQueue> oldFailedEmails = emailQueueRepository.findAll().stream()
            .filter(email -> email.getStatus() == EmailStatus.FAILED && 
                           email.getLastAttemptAt() != null && 
                           email.getLastAttemptAt().isBefore(cutoffDate))
            .toList();
        
        // 오래된 실패 이메일이 있을 때만 삭제 실행
        if (!oldFailedEmails.isEmpty()) {
            emailQueueRepository.deleteAll(oldFailedEmails);
            log.info("오래된 실패 이메일 정리 완료: {} 건", oldFailedEmails.size());
        }
    }
}