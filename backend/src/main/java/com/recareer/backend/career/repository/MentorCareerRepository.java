package com.recareer.backend.career.repository;

import com.recareer.backend.career.entity.MentorCareer;
import com.recareer.backend.mentor.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MentorCareerRepository extends JpaRepository<MentorCareer, Long> {
    
    @Query("SELECT c FROM MentorCareer c WHERE c.mentor = :mentor ORDER BY c.displayOrder ASC, c.startDate DESC")
    List<MentorCareer> findByMentorOrderByDisplayOrderAscStartDateDesc(@Param("mentor") Mentor mentor);
}