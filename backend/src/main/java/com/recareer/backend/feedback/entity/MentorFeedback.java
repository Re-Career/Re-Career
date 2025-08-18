package com.recareer.backend.feedback.entity;

import com.recareer.backend.common.entity.BaseTimeEntity;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mentor_feedbacks")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorFeedback extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 피드백을 작성한 사용자

    @Column(nullable = false)
    private Integer rating; // 1~5 사이 평점

    @Column(columnDefinition = "TEXT")
    private String comment; // 피드백 내용

    @Builder.Default
    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible = true; // 피드백 공개 여부

    @Builder.Default
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0; // 좋아요 갯수

    @Builder.Default
    @Column(name = "dislike_count", nullable = false)
    private Integer dislikeCount = 0; // 싫어요 갯수
}