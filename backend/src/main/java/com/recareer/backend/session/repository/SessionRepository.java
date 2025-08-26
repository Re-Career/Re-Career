package com.recareer.backend.session.repository;

import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.session.entity.Session;
import com.recareer.backend.session.entity.SessionStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
  List<Session> findAllByUserId(Long userId);
  
  List<Session> findByMentor(Mentor mentor);
  
  List<Session> findAllByMentorUserId(Long mentorUserId);
  
  @Query("SELECT s FROM Session s WHERE s.sessionTime < :oneHourAgo AND s.status IN :statuses")
  List<Session> findExpiredSessions(@Param("oneHourAgo") LocalDateTime oneHourAgo, 
                                   @Param("statuses") List<SessionStatus> statuses);
}