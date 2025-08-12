package com.recareer.backend.email.repository;

import com.recareer.backend.email.entity.EmailQueue;
import com.recareer.backend.email.entity.EmailStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailQueueRepository extends JpaRepository<EmailQueue, Long> {
    
    List<EmailQueue> findByStatusOrderByCreatedDateAsc(EmailStatus status, Pageable pageable);
    
    @Query("SELECT e FROM EmailQueue e WHERE e.status = :status AND e.retryCount < :maxRetryCount AND e.lastAttemptAt < :retryAfter ORDER BY e.createdDate ASC")
    List<EmailQueue> findRetryableEmails(@Param("status") EmailStatus status, 
                                       @Param("maxRetryCount") Integer maxRetryCount,
                                       @Param("retryAfter") LocalDateTime retryAfter,
                                       Pageable pageable);
    
    boolean existsByReservationIdAndStatus(Long reservationId, EmailStatus status);
}