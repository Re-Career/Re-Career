package com.recareer.backend.mentor.dto;

import com.recareer.backend.mentor.entity.Mentor;
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
public class MentorCreateResponseDto {

    private Long id;
    private Long userId;
    private String position;
    private String description;
    private String introduction;
    private Integer experience;
    private MentoringType mentoringType;
    private List<String> skills;
    private Boolean isVerified;

    public static MentorCreateResponseDto from(Mentor mentor) {
        return MentorCreateResponseDto.builder()
                .id(mentor.getId())
                .userId(mentor.getUser().getId())
                .position(mentor.getPosition())
                .description(mentor.getDescription())
                .introduction(mentor.getIntroduction())
                .experience(mentor.getExperience())
                .mentoringType(mentor.getMentoringType())
                .skills(mentor.getSkills())
                .isVerified(mentor.getIsVerified())
                .build();
    }
}