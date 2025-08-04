package com.recareer.backend.user.entity;

import com.recareer.backend.common.entity.BaseTimeEntity;
import com.recareer.backend.personality.entity.PersonalityTag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_personality_tags")
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
}