package com.recareer.backend.mentoringRecord.repository;

import com.recareer.backend.mentoringRecord.entity.MentoringRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MentoringRecordRepository extends JpaRepository<MentoringRecord, Long> {
    Optional<MentoringRecord> findByReservationId(Long reservationId);
    
    boolean existsByReservationId(Long reservationId);
}