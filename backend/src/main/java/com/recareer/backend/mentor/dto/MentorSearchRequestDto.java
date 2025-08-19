package com.recareer.backend.mentor.dto;

import java.util.List;

public record MentorSearchRequestDto(
    String keyword,
    List<Long> positionIds,
    String experience,
    List<Long> provinceIds,
    List<Long> personalityTagIds
) {
    
    public MentorSearchRequestDto {
        // validation if needed
        if (keyword != null && keyword.isBlank()) {
            keyword = null;
        }
    }
}