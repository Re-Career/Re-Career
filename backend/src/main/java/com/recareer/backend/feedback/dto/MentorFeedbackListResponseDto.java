package com.recareer.backend.feedback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorFeedbackListResponseDto {
    private Integer totalFeedbacks;
    private List<MentorFeedbackResponseDto> feedbacks;
}
