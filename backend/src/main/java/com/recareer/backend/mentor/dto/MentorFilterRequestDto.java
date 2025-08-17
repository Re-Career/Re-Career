package com.recareer.backend.mentor.dto;

import com.recareer.backend.mentor.entity.MentoringType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorFilterRequestDto {
    
    private List<String> jobs;           // 직업 리스트
    private List<String> experiences;    // 경력 리스트 (예: ["1-3년", "4-6년"])
    private List<MentoringType> mentoringTypes; // 미팅 방식 리스트
}