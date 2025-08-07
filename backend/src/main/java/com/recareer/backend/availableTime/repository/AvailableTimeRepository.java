package com.recareer.backend.availableTime.repository;

import com.recareer.backend.availableTime.entity.AvailableTime;
import com.recareer.backend.mentor.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvailableTimeRepository extends JpaRepository<AvailableTime, Long> {
    
    List<AvailableTime> findByMentor(Mentor mentor);
}
