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
public class MentorCreateRequestDto {

    private Long userId;
    private Long jobId;
    private Long companyId;
    private Long regionId;
    private String description;
    private String introduction;
    private Integer experience;
    private MentoringType mentoringType;
    private List<String> skills;
}