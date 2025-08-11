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
    Optional<MentoringRecord> findByReservationId(Long reservationId);
    
    boolean existsByReservationId(Long reservationId);
    
    @Query("SELECT mr FROM MentoringRecord mr " +
           "JOIN FETCH mr.reservation r " +
           "JOIN FETCH r.user " +
           "JOIN FETCH r.mentor m " +
           "JOIN FETCH m.user " +
           "WHERE (r.user.id = :userId OR m.user.id = :userId) " +
           "AND r.status = 'COMPLETED' " +
           "ORDER BY mr.createdDate DESC")
    List<MentoringRecord> findCompletedMentoringRecordsByUserId(@Param("userId") Long userId);
}