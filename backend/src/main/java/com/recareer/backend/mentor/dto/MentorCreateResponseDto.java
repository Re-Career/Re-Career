package com.recareer.backend.mentor.dto;

import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.position.dto.PositionDto;
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
    private PositionDto position;
    private CompanyDto company;
    private ProvinceDto province;
    private CityDto city;
    private String description;
    private String introduction;
    private Integer experience;
    private List<SkillDto> skills;
    private Boolean isVerified;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PositionDto {
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
    public static class ProvinceDto {
        private Long id;
        private String name;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CityDto {
        private Long id;
        private String name;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SkillDto {
        private Long id;
        private String name;
    }

    public static MentorCreateResponseDto from(Mentor mentor) {
        return MentorCreateResponseDto.builder()
                .id(mentor.getId())
                .userId(mentor.getUser().getId())
                .position(mentor.getPositionEntity() != null ? PositionDto.builder()
                        .id(mentor.getPositionEntity().getId())
                        .name(mentor.getPositionEntity().getName())
                        .build() : null)
                .company(mentor.getCompany() != null ? CompanyDto.builder()
                        .id(mentor.getCompany().getId())
                        .name(mentor.getCompany().getName())
                        .build() : null)
                .province(mentor.getUser() != null && mentor.getUser().getProvince() != null ? ProvinceDto.builder()
                        .id(mentor.getUser().getProvince().getId())
                        .name(mentor.getUser().getProvince().getName())
                        .build() : null)
                .city(mentor.getUser() != null && mentor.getUser().getCity() != null ? CityDto.builder()
                        .id(mentor.getUser().getCity().getId())
                        .name(mentor.getUser().getCity().getName())
                        .build() : null)
                .description(mentor.getDescription())
                .introduction(mentor.getIntroduction())
                .experience(mentor.getExperience())
                .skills(List.of()) // 멘토 생성 시점에는 스킬 정보를 별도로 조회
                .isVerified(mentor.getIsVerified())
                .build();
    }
}