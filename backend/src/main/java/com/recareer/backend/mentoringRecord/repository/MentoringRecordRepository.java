package com.recareer.backend.mentoringRecord.repository;

import com.recareer.backend.mentoringRecord.entity.MentoringRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MentoringRecordRepository extends JpaRepository<MentoringRecord, Long> {
    Optional<MentoringRecord> findBySessionId(Long sessionId);
    
    boolean existsBySessionId(Long sessionId);
    
    @Query("SELECT mr FROM MentoringRecord mr " +
           "JOIN FETCH mr.session s " +
           "JOIN FETCH s.user " +
           "JOIN FETCH s.mentor m " +
           "JOIN FETCH m.user " +
           "WHERE (s.user.id = :userId OR m.user.id = :userId) " +
           "AND s.status = 'COMPLETED' " +
           "ORDER BY mr.createdDate DESC")
    List<MentoringRecord> findCompletedMentoringRecordsByUserId(@Param("userId") Long userId);
}