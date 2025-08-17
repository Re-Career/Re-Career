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
    private JobDto job;
    private CompanyDto company;
    private RegionDto region;
    private String description;
    private String introduction;
    private List<String> skills;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class JobDto {
        private Long id;
        private String name;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CompanyDto {
        private Long id;
        private String name;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RegionDto {
        private Long id;
        private String name;
    }

    public static MentorUpdateResponseDto from(Mentor mentor) {
        return MentorUpdateResponseDto.builder()
                .id(mentor.getId())
                .job(mentor.getJob() != null ? JobDto.builder()
                        .id(mentor.getJob().getId())
                        .name(mentor.getJob().getName())
                        .build() : null)
                .company(mentor.getCompany() != null ? CompanyDto.builder()
                        .id(mentor.getCompany().getId())
                        .name(mentor.getCompany().getName())
                        .build() : null)
                .region(mentor.getUser() != null && mentor.getUser().getProvince() != null ? RegionDto.builder()
                        .id(mentor.getUser().getProvince().getId())
                        .name(mentor.getUser().getProvince().getName())
                        .build() : null)
                .description(mentor.getDescription())
                .introduction(mentor.getIntroduction())
                .skills(mentor.getSkills())
                .build();
    }
}