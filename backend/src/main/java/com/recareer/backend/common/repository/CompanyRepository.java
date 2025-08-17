package com.recareer.backend.common.repository;

import com.recareer.backend.common.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {}