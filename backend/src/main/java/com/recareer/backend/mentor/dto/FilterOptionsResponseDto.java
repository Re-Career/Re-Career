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
public class FilterOptionsResponseDto {
    private String key;
    private String title;
    private List<FilterOptionDto> options;
}