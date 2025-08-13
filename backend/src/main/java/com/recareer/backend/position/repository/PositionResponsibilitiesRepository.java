package com.recareer.backend.position.repository;

import com.recareer.backend.position.entity.PositionResponsibilities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionResponsibilitiesRepository extends JpaRepository<PositionResponsibilities, Long> {

}