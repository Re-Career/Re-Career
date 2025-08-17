package com.recareer.backend.feedback.repository;

import com.recareer.backend.feedback.entity.MentorFeedback;
import com.recareer.backend.mentor.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MentorFeedbackRepository extends JpaRepository<MentorFeedback, Long> {
    
    @Query("SELECT f FROM MentorFeedback f JOIN FETCH f.user WHERE f.mentor = :mentor AND f.isVisible = true ORDER BY f.createdDate DESC")
    List<MentorFeedback> findByMentorAndIsVisibleTrueWithUser(@Param("mentor") Mentor mentor);
    
    @Query("SELECT AVG(f.rating) FROM MentorFeedback f WHERE f.mentor = :mentor AND f.isVisible = true")
    Double getAverageRatingByMentor(@Param("mentor") Mentor mentor);
    
    @Query("SELECT COUNT(f) FROM MentorFeedback f WHERE f.mentor = :mentor AND f.isVisible = true")
    Integer countByMentorAndIsVisibleTrue(@Param("mentor") Mentor mentor);
    
    @Query("SELECT f FROM MentorFeedback f JOIN FETCH f.user WHERE f.mentor.id = :mentorId AND f.isVisible = true ORDER BY f.createdDate DESC")
    List<MentorFeedback> findByMentorIdAndIsVisibleTrue(@Param("mentorId") Long mentorId);
}