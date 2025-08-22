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
    
    @Query("SELECT m FROM Mentor m JOIN m.user u WHERE m.id = :id AND u.role = 'MENTOR'")
    Optional<Mentor> findById(@Param("id") Long id);
    
    @Query("SELECT m FROM Mentor m JOIN FETCH m.user u WHERE u.role = 'MENTOR'")
    List<Mentor> findAllWithUser();
    
    @Query("SELECT m.position.name, COUNT(m) FROM Mentor m JOIN m.user u WHERE u.role = 'MENTOR' AND u.province.id = :provinceId GROUP BY m.position.name ORDER BY COUNT(m) DESC limit 4")
    List<Object[]> countPositionsByProvinceId(@Param("provinceId") Long provinceId);

    @Query("SELECT m FROM Mentor m JOIN FETCH m.user u WHERE u.role = 'MENTOR' AND u.province.id = :provinceId")
    List<Mentor> findByUserProvinceIdWithUser(@Param("provinceId") Long provinceId);

    @Query("SELECT m FROM Mentor m JOIN m.user u WHERE u.role = 'MENTOR' AND u.province.id = :provinceId")
    List<Mentor> findByUserProvinceId(@Param("provinceId") Long provinceId);
}
