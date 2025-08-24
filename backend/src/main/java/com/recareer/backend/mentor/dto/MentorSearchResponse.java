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
    
    @Schema(description = "사용자 맞춤 추천 멘토 리스트", example = "[]")
    private List<MentorCard> recommendedList;
    
    @Schema(description = "검색 조건 기반 멘토 리스트", example = "[]")
    private List<MentorCard> searchedList;
    
    public static MentorSearchResponse of(List<MentorCard> recommendedList, List<MentorCard> searchedList) {
        return MentorSearchResponse.builder()
                .recommendedList(recommendedList)
                .searchedList(searchedList)
                .build();
    }
}