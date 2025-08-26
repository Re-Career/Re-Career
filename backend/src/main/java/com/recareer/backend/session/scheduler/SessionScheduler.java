package com.recareer.backend.session.scheduler;

import com.recareer.backend.session.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionScheduler {

    private final SessionService sessionService;

    // 10분마다 체크
    @Scheduled(fixedRate = 600000)
    public void completeExpiredSessions() {
        log.info("세션 시작 1시간 후 자동 완료 처리 배치 작업 시작");
        try {
            int completedCount = sessionService.completeExpiredSessions();
            log.info("세션 시작 1시간 후 자동 완료 처리 배치 작업 종료 - 처리된 세션 수: {}", completedCount);
        } catch (Exception e) {
            log.error("세션 자동 완료 처리 중 오류 발생", e);
        }
    }
}