package com.recareer.backend.availableTime.repository;

import com.recareer.backend.availableTime.entity.AvailableTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailableTimeRepository extends JpaRepository<AvailableTime, Long> {
}
