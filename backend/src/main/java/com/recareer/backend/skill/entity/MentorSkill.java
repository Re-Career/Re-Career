package com.recareer.backend.skill.entity;

import com.recareer.backend.mentor.entity.Mentor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mentor_skills", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"mentor_id", "skill_id"}))
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    private Mentor mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id")
    private Skill skill;
}