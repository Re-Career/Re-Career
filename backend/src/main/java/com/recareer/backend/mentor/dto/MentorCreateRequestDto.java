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
    private String position;
    private String description;
    private Integer experience;
    private MentoringType mentoringType;
    private List<String> skills;
}