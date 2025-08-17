package com.recareer.backend.feedback.dto;

import com.recareer.backend.feedback.entity.MentorFeedback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorFeedbackResponseDto {

    private Long id;
    private String userName; // 피드백 작성자 이름
    private String userProfileImage; // 피드백 작성자 프로필 사진
    private Integer rating; // 1~5 사이 평점
    private String comment; // 피드백 내용
    private Integer likeCount; // 좋아요 갯수
    private Integer dislikeCount; // 싫어요 갯수
    private LocalDateTime createdAt; // 피드백 작성 시간

    public static MentorFeedbackResponseDto from(MentorFeedback feedback) {
        return MentorFeedbackResponseDto.builder()
                .id(feedback.getId())
                .userName(feedback.getUser().getName())
                .userProfileImage(feedback.getUser().getProfileImageUrl())
                .rating(feedback.getRating())
                .comment(feedback.getComment())
                .likeCount(feedback.getLikeCount())
                .dislikeCount(feedback.getDislikeCount())
                .createdAt(feedback.getCreatedDate())
                .build();
    }
}