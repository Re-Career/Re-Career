package com.recareer.backend.mentor.dto;

import com.recareer.backend.career.entity.MentorCareer;
import com.recareer.backend.feedback.entity.MentorFeedback;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.position.dto.PositionDto;
import com.recareer.backend.skill.entity.MentorSkill;
import com.recareer.backend.user.entity.UserPersonalityTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorDetailResponseDto {

    private Long id;
    private String name;
    private PositionDto position;
    private String email;
    private String profileImageUrl;
    private CompanyDto company;
    private Integer experience;
    private ProvinceDto province;
    private CityDto city;
    private String meetingType;
    private List<PersonalityTagDto> personalityTags;
    private String shortDescription;
    private String introduction;
    private List<SkillDto> skills;
    private List<String> career;
    private FeedbackDto feedback;


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
    public static class PersonalityTagDto {
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

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FeedbackDto {
        private Double rating;
        private Integer count;
        private List<CommentDto> comments;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CommentDto {
        private Long id;
        private String user;
        private Integer rating; // 1~5 사이 정수값
        private String comment;
        private String date;
    }

    public static MentorDetailResponseDto from(Mentor mentor, List<MentorCareer> careers, List<MentorFeedback> feedbacks, Double averageRating, Integer feedbackCount, List<UserPersonalityTag> userPersonalityTags, List<MentorSkill> mentorSkills) {
        String meetingType = "온라인"; // 미팅 방식은 온라인으로 통일
        
        // 경력 정보 변환
        List<String> careerList = careers.stream()
                .map(career -> {
                    String period = formatCareerPeriod(career);
                    return String.format("%s: %s %s", period, career.getCompany(), career.getPosition());
                })
                .toList();
        
        // 피드백 정보 변환
        List<CommentDto> comments = feedbacks.stream()
                .map(feedback -> CommentDto.builder()
                        .id(feedback.getId())
                        .user(feedback.getUser().getName())
                        .rating(feedback.getRating())
                        .comment(feedback.getComment())
                        .date(feedback.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .build())
                .toList();

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
        
        return MentorDetailResponseDto.builder()
                .id(mentor.getId())
                .name(mentor.getUser().getName())
                .position(mentor.getPositionEntity() != null ? PositionDto.builder()
                        .id(mentor.getPositionEntity().getId())
                        .name(mentor.getPositionEntity().getName())
                        .build() : null)
                .email(mentor.getUser().getEmail())
                .profileImageUrl(mentor.getUser().getProfileImageUrl())
                .company(companyDto)
                .experience(mentor.getExperience())
                .province(mentor.getUser() != null && mentor.getUser().getProvince() != null ? ProvinceDto.builder()
                        .id(mentor.getUser().getProvince().getId())
                        .name(mentor.getUser().getProvince().getName())
                        .build() : null)
                .city(mentor.getUser() != null && mentor.getUser().getCity() != null ? CityDto.builder()
                        .id(mentor.getUser().getCity().getId())
                        .name(mentor.getUser().getCity().getName())
                        .build() : null)
                .meetingType(meetingType)
                .personalityTags(personalityTagDtos)
                .shortDescription(mentor.getDescription())
                .introduction(mentor.getIntroduction())
                .skills(mentorSkills != null ? 
                        mentorSkills.stream()
                                .map(mentorSkill -> SkillDto.builder()
                                        .id(mentorSkill.getSkill().getId())
                                        .name(mentorSkill.getSkill().getName())
                                        .build())
                                .toList() : List.of())
                .career(careerList)
                .feedback(FeedbackDto.builder()
                        .rating(averageRating != null ? averageRating : 0.0)
                        .count(feedbackCount != null ? feedbackCount : 0)
                        .comments(comments)
                        .build())
                .build();
    }
    
    private static String formatCareerPeriod(MentorCareer career) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        String startYear = career.getStartDate().format(formatter);
        
        if (career.getIsCurrent() || career.getEndDate() == null) {
            return startYear + "-현재";
        } else {
            String endYear = career.getEndDate().format(formatter);
            return startYear + "-" + endYear;
        }
    }

}