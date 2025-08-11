package com.recareer.backend.position.repository;

import com.recareer.backend.position.entity.PositionResponsibilityMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionResponsibilityMapRepository extends JpaRepository<PositionResponsibilityMap, Long> {
    List<PositionResponsibilityMap> findByPositionId(Long positionId);
}