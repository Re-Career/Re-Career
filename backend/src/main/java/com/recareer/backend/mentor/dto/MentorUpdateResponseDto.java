package com.recareer.backend.mentor.dto;

import com.recareer.backend.mentor.entity.Mentor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorUpdateResponseDto {

    private Long id;
    private String position;
    private String description;
    private String introduction;
    private List<String> skills;

    public static MentorUpdateResponseDto from(Mentor mentor) {
        return MentorUpdateResponseDto.builder()
                .id(mentor.getId())
                .position(mentor.getPosition())
                .description(mentor.getDescription())
                .introduction(mentor.getIntroduction())
                .skills(mentor.getSkills())
                .build();
    }
}