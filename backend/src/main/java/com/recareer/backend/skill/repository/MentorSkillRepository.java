package com.recareer.backend.skill.repository;

import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.skill.entity.MentorSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MentorSkillRepository extends JpaRepository<MentorSkill, Long> {
    
    @Query("SELECT ms FROM MentorSkill ms JOIN FETCH ms.skill WHERE ms.mentor = :mentor")
    List<MentorSkill> findByMentorWithSkill(@Param("mentor") Mentor mentor);
    
    @Modifying
    @Transactional
    void deleteByMentor(Mentor mentor);
}