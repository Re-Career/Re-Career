package com.recareer.backend.mentor.dto;

import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.user.entity.UserPersonalityTag;
import com.recareer.backend.career.entity.MentorCareer;
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
@Schema(description = "멘토 카드 정보")
public class MentorCard {

    @Schema(description = "멘토 ID", example = "1")
    private Long id;
    
    @Schema(description = "멘토 이름", example = "김멘토")
    private String name;
    
    @Schema(description = "직업 정보")
    private PositionDto position;
    
    @Schema(description = "경력 (년)", example = "5")
    private Integer experience;
    
    @Schema(description = "지역 정보")
    private ProvinceDto province;
    
    @Schema(description = "회사 정보")
    private CompanyDto company;
    
    @Schema(description = "성향 태그 목록")
    private List<PersonalityTagDto> personalityTag;
    
    @Schema(description = "프로필 이미지 URL", example = "https://example.com/image.jpg")
    private String profileImageUrl;

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
    public static class ProvinceDto {
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
    public static class PersonalityTagDto {
        private Long id;
        private String name;
    }

    public static MentorCard from(Mentor mentor, List<UserPersonalityTag> userPersonalityTags,
                                  List<MentorCareer> careers) {

        // 성향 태그 변환
        List<PersonalityTagDto> personalityTagDtos = userPersonalityTags.stream()
                .map(upt -> PersonalityTagDto.builder()
                        .id(upt.getPersonalityTag().getId())
                        .name(upt.getPersonalityTag().getName())
                        .build())
                .toList();

        // 가장 최신 회사 정보 (가장 최근에 추가된 career)
        CompanyDto companyDto = null;
        if (careers != null && !careers.isEmpty()) {
            MentorCareer latestCareer = careers.get(careers.size() - 1); // 가장 마지막 career
            if (latestCareer.getCompany() != null) {
                companyDto = CompanyDto.builder()
                        .id(latestCareer.getId()) // career의 ID 사용
                        .name(latestCareer.getCompany()) // company는 String
                        .build();
            }
        }

        return MentorCard.builder()
                .id(mentor.getId())
                .name(mentor.getUser().getName())
                .position(mentor.getPositionEntity() != null ? PositionDto.builder()
                        .id(mentor.getPositionEntity().getId())
                        .name(mentor.getPositionEntity().getName())
                        .build() : null)
                .experience(mentor.getExperience())
                .province(mentor.getUser() != null && mentor.getUser().getProvince() != null ? ProvinceDto.builder()
                        .id(mentor.getUser().getProvince().getId())
                        .name(mentor.getUser().getProvince().getName())
                        .build() : null)
                .company(companyDto)
                .personalityTag(personalityTagDtos)
                .profileImageUrl(mentor.getUser().getProfileImageUrl())
                .build();
    }
}