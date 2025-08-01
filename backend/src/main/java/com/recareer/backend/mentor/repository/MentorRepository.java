package com.recareer.backend.mentor.repository;

import com.recareer.backend.mentor.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorRepository extends JpaRepository<Mentor, Long> {}
