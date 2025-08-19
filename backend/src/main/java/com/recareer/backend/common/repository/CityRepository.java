package com.recareer.backend.common.repository;

import com.recareer.backend.common.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {
    @Query("SELECT c FROM City c WHERE c.province.id = :provinceId")
    List<City> findByProvinceId(@Param("provinceId") Long provinceId);

    Optional<City> findByName(String name);
}