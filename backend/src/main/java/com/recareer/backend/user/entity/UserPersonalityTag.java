package com.recareer.backend.user.entity;

import com.recareer.backend.common.entity.BaseTimeEntity;
import com.recareer.backend.personality.entity.PersonalityTag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "user_personality_tags", 
       uniqueConstraints = {
           @UniqueConstraint(
               name = "uk_user_personality_tag", 
               columnNames = {"user_id", "personality_tag_id"}
           )
       })
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPersonalityTag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "personality_tag_id", nullable = false)
    private PersonalityTag personalityTag;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        UserPersonalityTag that = (UserPersonalityTag) obj;
        return Objects.equals(user != null ? user.getId() : null, 
                             that.user != null ? that.user.getId() : null) &&
               Objects.equals(personalityTag != null ? personalityTag.getId() : null, 
                             that.personalityTag != null ? that.personalityTag.getId() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            user != null ? user.getId() : null,
            personalityTag != null ? personalityTag.getId() : null
        );
    }
}