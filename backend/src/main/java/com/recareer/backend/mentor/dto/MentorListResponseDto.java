package com.recareer.backend.mentor.dto;

import com.recareer.backend.mentor.entity.Mentor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorListResponseDto {

    private Long id;
    private String name;
    private String profileImageUrl;
    private String region;
    private String position;
    private String description;

    public static MentorListResponseDto from(Mentor mentor) {
        return MentorListResponseDto.builder()
                .id(mentor.getId())
                .name(mentor.getUser().getName())
                .profileImageUrl(mentor.getUser().getProfileImageUrl())
                .region(mentor.getUser().getRegion())
                .position(mentor.getPosition())
                .description(mentor.getDescription())
                .build();
    }
}