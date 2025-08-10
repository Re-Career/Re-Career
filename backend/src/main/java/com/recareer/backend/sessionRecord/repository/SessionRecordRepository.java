package com.recareer.backend.sessionRecord.repository;

import com.recareer.backend.sessionRecord.entity.SessionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRecordRepository extends JpaRepository<SessionRecord, Long> {
    Optional<SessionRecord> findByReservationId(Long reservationId);
}