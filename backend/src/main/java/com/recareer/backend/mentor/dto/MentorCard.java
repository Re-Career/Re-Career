package com.recareer.backend.mentor.dto;

import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.user.entity.UserPersonalityTag;
import com.recareer.backend.career.entity.MentorCareer;
import com.recareer.backend.feedback.entity.MentorFeedback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorCard {

    private Long id;
    private String name;
    private PositionDto position;
    private Integer experience;
    private ProvinceDto province;
    private List<PersonalityTagDto> personalityTag;
    private String profileImageUrl;
    private Double rating;
    private Integer reviewCount;
    private String shortIntroduction;

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
    public static class PersonalityTagDto {
        private Long id;
        private String name;
    }

    public static MentorCard from(Mentor mentor, List<UserPersonalityTag> userPersonalityTags,
                                  List<MentorCareer> careers, Double averageRating, Integer reviewCount) {

        // 성향 태그 변환
        List<PersonalityTagDto> personalityTagDtos = userPersonalityTags.stream()
                .map(upt -> PersonalityTagDto.builder()
                        .id(upt.getPersonalityTag().getId())
                        .name(upt.getPersonalityTag().getName())
                        .build())
                .toList();

        // shortIntro는 introduction의 앞부분 또는 description 사용
        String shortIntro = "";
        if (mentor.getIntroduction() != null && !mentor.getIntroduction().isEmpty()) {
            shortIntro = mentor.getIntroduction().length() > 100
                    ? mentor.getIntroduction().substring(0, 100) + "..."
                    : mentor.getIntroduction();
        } else if (mentor.getDescription() != null && !mentor.getDescription().isEmpty()) {
            shortIntro = mentor.getDescription().length() > 100
                    ? mentor.getDescription().substring(0, 100) + "..."
                    : mentor.getDescription();
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
                .personalityTag(personalityTagDtos)
                .profileImageUrl(mentor.getUser().getProfileImageUrl())
                .rating(averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : null)
                .reviewCount(reviewCount != null ? reviewCount : 0)
                .shortIntroduction(shortIntro)
                .build();
    }
}