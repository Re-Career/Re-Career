package com.recareer.backend.common.repository;

import com.recareer.backend.common.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {}