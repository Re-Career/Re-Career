package com.recareer.backend.mentor.repository;

import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MentorRepository extends JpaRepository<Mentor, Long> {
    Optional<Mentor> findByUser(User user);
    
    @Query("SELECT m FROM Mentor m JOIN m.user u WHERE m.id = :id AND m.isVerified = true AND u.role = 'MENTOR'")
    Optional<Mentor> findById(@Param("id") Long id);
    
    @Query("SELECT m FROM Mentor m JOIN m.user u LEFT JOIN u.province p WHERE m.isVerified = true AND u.role = 'MENTOR' AND (p.name LIKE %:region% OR p IS NULL)")
    List<Mentor> findByIsVerifiedTrueAndUserRegionContains(@Param("region") String region);
    
    @Query("SELECT m FROM Mentor m JOIN FETCH m.user u LEFT JOIN FETCH u.province p WHERE m.isVerified = true AND u.role = 'MENTOR' AND (p.name LIKE %:region% OR p IS NULL)")
    List<Mentor> findByIsVerifiedTrueAndUserRegionContainsWithUser(@Param("region") String region);
    
    @Query("SELECT m FROM Mentor m JOIN FETCH m.user u WHERE m.isVerified = true AND u.role = 'MENTOR'")
    List<Mentor> findAllByIsVerifiedTrueWithUser();
    
    @Query("SELECT m.job.name, COUNT(m) FROM Mentor m JOIN m.user u LEFT JOIN u.province p WHERE m.isVerified = true AND u.role = 'MENTOR' AND (p.name LIKE %:region% OR p IS NULL) GROUP BY m.job.name ORDER BY COUNT(m) DESC limit 4")
    List<Object[]> countPositionsByRegion(@Param("region") String region);
}
