package com.recareer.backend.common.repository;

import com.recareer.backend.common.entity.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProvinceRepository extends JpaRepository<Province, Long> {

    @Query("SELECT p FROM Province p JOIN FETCH p.cities")
    List<Province> findAllWithCities();
}