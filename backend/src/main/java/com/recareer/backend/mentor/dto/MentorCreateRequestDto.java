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
public class MentorCreateRequestDto {

    private Long userId;
    private Long positionId;
    private Long companyId;
    private String description;
    private String introduction;
    private Integer experience;
    private List<Long> skillIds;
}