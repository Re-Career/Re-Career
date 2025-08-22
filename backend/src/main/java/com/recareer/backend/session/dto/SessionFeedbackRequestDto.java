package com.recareer.backend.session.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionFeedbackRequestDto {
    
    @NotBlank(message = "멘티 피드백은 필수입니다")
    private String menteeFeedback;
}