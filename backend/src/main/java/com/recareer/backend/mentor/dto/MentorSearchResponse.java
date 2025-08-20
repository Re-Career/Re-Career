package com.recareer.backend.mentor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "멘토 검색 응답 DTO")
public class MentorSearchResponse {
    
    @Schema(description = "1순위: 지역/성향, 2순위: 직업/경험 필터링 결과", example = "[]")
    private List<MentorCard> primary;
    
    @Schema(description = "직업/경험 기준 필터링 결과", example = "[]")
    private List<MentorCard> secondary;
    
    public static MentorSearchResponse of(List<MentorCard> primary, List<MentorCard> secondary) {
        return MentorSearchResponse.builder()
                .primary(primary)
                .secondary(secondary)
                .build();
    }
}