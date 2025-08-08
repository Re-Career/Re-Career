package com.recareer.backend.sessionRecord.repository;

import com.recareer.backend.sessionRecord.entity.SessionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRecordRepository extends JpaRepository<SessionRecord, Long> {}
