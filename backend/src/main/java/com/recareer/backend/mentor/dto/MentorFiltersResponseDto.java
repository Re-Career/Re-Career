package com.recareer.backend.mentor.dto;

import java.util.List;

public record MentorFiltersResponseDto(
    List<FilterOptionDto> positions,
    List<FilterOptionDto> provinces,
    List<FilterOptionDto> personalityTags
) {
}