package com.recareer.backend.user.repository;

import com.recareer.backend.user.entity.UserPersonalityTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPersonalityTagRepository extends JpaRepository<UserPersonalityTag, Long> {
    
    List<UserPersonalityTag> findByUserId(Long userId);
    
    @Modifying
    @Query("DELETE FROM UserPersonalityTag upt WHERE upt.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
    
    int countByUserId(Long userId);
}