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
public class MentorListResponse {
    private List<MentorCard> mentors;
    
    public static MentorListResponse of(List<MentorCard> mentors) {
        return MentorListResponse.builder()
                .mentors(mentors)
                .build();
    }
}