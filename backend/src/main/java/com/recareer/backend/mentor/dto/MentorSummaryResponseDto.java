package com.recareer.backend.mentor.dto;

import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.user.entity.UserPersonalityTag;
import com.recareer.backend.career.entity.MentorCareer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorSummaryResponseDto {

    private Long id;
    private String name;
    private JobDto job;
    private String email;
    private String profileImageUrl;
    private CompanyDto company;
    private Integer experience;
    private RegionDto region;
    private String meetingType;
    private List<PersonalityTagDto> personalityTags;

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

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PersonalityTagDto {
        private Long id;
        private String name;
    }

    public static MentorSummaryResponseDto from(Mentor mentor, List<UserPersonalityTag> userPersonalityTags, List<MentorCareer> careers) {
        String meetingType = "온라인"; // 미팅 방식은 온라인으로 통일
        
        // 성향 태그 변환
        List<PersonalityTagDto> personalityTagDtos = userPersonalityTags.stream()
                .map(upt -> PersonalityTagDto.builder()
                        .id(upt.getPersonalityTag().getId())
                        .name(upt.getPersonalityTag().getName())
                        .build())
                .toList();

        // 현재 회사 정보 (멘토의 회사 또는 가장 최신 경력에서 추출)
        CompanyDto companyDto = null;
        if (mentor.getCompany() != null) {
            companyDto = CompanyDto.builder()
                    .id(mentor.getCompany().getId())
                    .name(mentor.getCompany().getName())
                    .build();
        } else {
            // 멘토에 회사 정보가 없으면 최신 경력에서 추출 (기존 로직 유지)
            String currentCompanyName = careers.stream()
                    .filter(MentorCareer::getIsCurrent)
                    .findFirst()
                    .map(MentorCareer::getCompany)
                    .orElse(null);
            if (currentCompanyName != null) {
                companyDto = CompanyDto.builder()
                        .id(null) // 경력에서 가져온 회사는 ID가 없음
                        .name(currentCompanyName)
                        .build();
            }
        }

        return MentorSummaryResponseDto.builder()
                .id(mentor.getId())
                .name(mentor.getUser().getName())
                .job(mentor.getJob() != null ? JobDto.builder()
                        .id(mentor.getJob().getId())
                        .name(mentor.getJob().getName())
                        .build() : null)
                .email(mentor.getUser().getEmail())
                .profileImageUrl(mentor.getUser().getProfileImageUrl()) // nullable
                .company(companyDto)
                .experience(mentor.getExperience())
                .region(mentor.getUser() != null && mentor.getUser().getProvince() != null ? RegionDto.builder()
                        .id(mentor.getUser().getProvince().getId())
                        .name(mentor.getUser().getProvince().getName())
                        .build() : null)
                .meetingType(meetingType)
                .personalityTags(personalityTagDtos)
                .build();
    }

}