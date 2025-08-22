package com.recareer.backend.skill.entity;

import com.recareer.backend.common.entity.BaseTimeEntity;
import com.recareer.backend.mentor.entity.Mentor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "mentor_skills", 
       uniqueConstraints = {
           @UniqueConstraint(
               name = "uk_mentor_skill", 
               columnNames = {"mentor_id", "skill_id"}
           )
       })
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorSkill extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        MentorSkill that = (MentorSkill) obj;
        return Objects.equals(mentor != null ? mentor.getId() : null, 
                             that.mentor != null ? that.mentor.getId() : null) &&
               Objects.equals(skill != null ? skill.getId() : null, 
                             that.skill != null ? that.skill.getId() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            mentor != null ? mentor.getId() : null,
            skill != null ? skill.getId() : null
        );
    }
}