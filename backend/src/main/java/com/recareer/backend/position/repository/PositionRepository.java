package com.recareer.backend.position.repository;

import com.recareer.backend.position.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findByTrendRankIsNotNullAndTrendRankLessThanEqualOrderByTrendRankAsc(Integer trendRank);
    
    @Query(value = "SELECT * FROM positions WHERE trend_rank IS NULL ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Position> findRandomPositionsWithNullTrendRank(@Param("limit") int limit);
    
    List<Position> findByNameIn(List<String> names);

    Optional<Position> findByName(String name);
}