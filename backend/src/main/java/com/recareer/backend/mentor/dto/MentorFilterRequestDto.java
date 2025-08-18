package com.recareer.backend.mentor.dto;

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
    private Long provinceId;             // 시/도 ID
    private Long cityId;                 // 시/군/구 ID
}