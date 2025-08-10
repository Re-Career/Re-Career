package com.recareer.backend.sessionRecord.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionRecordRequestDto {

    // 멘티가 남기는 상담 후 피드백
    @Size(max = 5000, message = "멘티 피드백은 5000자를 초과할 수 없습니다.")
    private String menteeFeedback;
}