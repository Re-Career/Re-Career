package com.recareer.backend.mentor.dto;

import com.recareer.backend.career.entity.MentorCareer;
import com.recareer.backend.feedback.entity.MentorFeedback;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.entity.MentoringType;
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
    private String job;
    private String email;
    private String profileImageUrl;
    private String company;
    private Integer experience;
    private String location;
    private String meetingType;
    private String personality;
    private String shortDescription;
    private String introduction;
    private List<String> skills;
    private List<String> career;
    private FeedbackDto feedback;

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

    public static MentorDetailResponseDto from(Mentor mentor, List<MentorCareer> careers, List<MentorFeedback> feedbacks, Double averageRating, Integer feedbackCount) {
        String meetingType = convertMentoringType(mentor.getMentoringType());
        
        // 경력 정보 변환
        List<String> careerList = careers.stream()
                .map(career -> {
                    String period = formatCareerPeriod(career);
                    return String.format("%s - %s (%s)", career.getCompany(), career.getPosition(), period);
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
        
        // 현재 회사 정보 (가장 최신 경력에서 추출)
        String currentCompany = careers.stream()
                .filter(MentorCareer::getIsCurrent)
                .findFirst()
                .map(MentorCareer::getCompany)
                .orElse(null);
        
        return MentorDetailResponseDto.builder()
                .id(mentor.getId())
                .name(mentor.getUser().getName())
                .job(mentor.getPosition())
                .email(mentor.getUser().getEmail())
                .profileImageUrl(mentor.getUser().getProfileImageUrl())
                .company(currentCompany)
                .experience(mentor.getExperience())
                .location(mentor.getUser().getRegion())
                .meetingType(meetingType)
                .personality(null) // 추후 PersonalityTag 매핑 로직 추가
                .shortDescription(mentor.getDescription())
                .introduction(mentor.getDescription()) // 현재는 description을 introduction으로 사용
                .skills(mentor.getSkills() != null ? mentor.getSkills() : List.of())
                .career(careerList)
                .feedback(FeedbackDto.builder()
                        .rating(averageRating != null ? averageRating : 0.0)
                        .count(feedbackCount != null ? feedbackCount : 0)
                        .comments(comments)
                        .build())
                .build();
    }
    
    private static String formatCareerPeriod(MentorCareer career) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM");
        String startDate = career.getStartDate().format(formatter);
        
        if (career.getIsCurrent() || career.getEndDate() == null) {
            return startDate + " ~ 현재";
        } else {
            String endDate = career.getEndDate().format(formatter);
            return startDate + " ~ " + endDate;
        }
    }

    private static String convertMentoringType(MentoringType mentoringType) {
        if (mentoringType == null) {
            return null;
        }
        return switch (mentoringType) {
            case ONLINE -> "online";
            case OFFLINE -> "offline";
            case BOTH -> "both";
        };
    }
}