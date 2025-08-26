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
    
    /**
     * N+1 문제 해결을 위한 배치 조회 메소드
     * 여러 멘토의 경력 정보를 한번에 조회
     * 
     * @param mentorIds 멘토 ID 리스트
     * @return 모든 멘토의 경력 리스트 (정렬됨)
     */
    @Query("SELECT c FROM MentorCareer c WHERE c.mentor.id IN :mentorIds ORDER BY c.displayOrder ASC, c.startDate DESC")
    List<MentorCareer> findByMentorIdInOrderByDisplayOrder(@Param("mentorIds") List<Long> mentorIds);
}