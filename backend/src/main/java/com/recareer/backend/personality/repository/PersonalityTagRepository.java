package com.recareer.backend.personality.repository;

import com.recareer.backend.personality.entity.PersonalityTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalityTagRepository extends JpaRepository<PersonalityTag, Long> {
}