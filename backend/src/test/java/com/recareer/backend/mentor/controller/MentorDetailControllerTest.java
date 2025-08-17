package com.recareer.backend.mentor.controller;

import com.recareer.backend.career.entity.MentorCareer;
import com.recareer.backend.career.repository.MentorCareerRepository;
import com.recareer.backend.feedback.entity.MentorFeedback;
import com.recareer.backend.feedback.repository.MentorFeedbackRepository;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.entity.MentoringType;
import com.recareer.backend.mentor.repository.MentorRepository;
import com.recareer.backend.user.entity.Role;
import com.recareer.backend.user.entity.User;
import com.recareer.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@Transactional
class MentorDetailControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MentorCareerRepository mentorCareerRepository;

    @Autowired
    private MentorFeedbackRepository mentorFeedbackRepository;

    private User mentorUser;
    private Mentor mentor;
    private User reviewUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // 멘토 사용자 생성
        mentorUser = User.builder()
                .name("김멘토")
                .email("mentor@test.com")
                .role(Role.MENTOR)
                .provider("google")
                .providerId("mentor123")
                .profileImageUrl("https://example.com/mentor.jpg")
                .region("서울시 강남구")
                .build();
        userRepository.save(mentorUser);

        // 멘토 생성
        mentor = Mentor.builder()
                .id(mentorUser.getId())
                .user(mentorUser)
                .position("시니어 백엔드 개발자")
                .description("5년차 백엔드 개발자로 Spring Boot, JPA 전문가입니다.")
                .experience(5)
                .mentoringType(MentoringType.BOTH)
                .isVerified(true)
                .build();
        mentorRepository.save(mentor);

        // 리뷰 작성자 생성
        reviewUser = User.builder()
                .name("리뷰어")
                .email("reviewer@test.com")
                .role(Role.MENTEE)
                .provider("google")
                .providerId("reviewer123")
                .build();
        userRepository.save(reviewUser);
    }

    @Test
    @DisplayName("멘토 상세 조회 - 경력과 피드백 없는 경우")
    void getMentorById_WithoutCareerAndFeedback() throws Exception {
        mockMvc.perform(get("/mentors/{id}", mentor.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(mentor.getId()))
                .andExpect(jsonPath("$.data.name").value("김멘토"))
                .andExpect(jsonPath("$.data.job").value("시니어 백엔드 개발자"))
                .andExpect(jsonPath("$.data.email").value("mentor@test.com"))
                .andExpect(jsonPath("$.data.profileImageUrl").value("https://example.com/mentor.jpg"))
                .andExpect(jsonPath("$.data.company").isEmpty())
                .andExpect(jsonPath("$.data.experience").value(5))
                .andExpect(jsonPath("$.data.location").value("서울시 강남구"))
                .andExpect(jsonPath("$.data.meetingType").value("both"))
                .andExpect(jsonPath("$.data.shortDescription").value("5년차 백엔드 개발자로 Spring Boot, JPA 전문가입니다."))
                .andExpect(jsonPath("$.data.introduction").value("5년차 백엔드 개발자로 Spring Boot, JPA 전문가입니다."))
                .andExpect(jsonPath("$.data.skills").isArray())
                .andExpect(jsonPath("$.data.skills").isEmpty())
                .andExpect(jsonPath("$.data.career").isArray())
                .andExpect(jsonPath("$.data.career").isEmpty())
                .andExpect(jsonPath("$.data.feedback.rating").value(0.0))
                .andExpect(jsonPath("$.data.feedback.count").value(0))
                .andExpect(jsonPath("$.data.feedback.comments").isArray())
                .andExpect(jsonPath("$.data.feedback.comments").isEmpty())
                .andDo(print());
    }

    @Test
    @DisplayName("멘토 상세 조회 - 경력 정보 포함")
    void getMentorById_WithCareer() throws Exception {
        // 경력 정보 추가
        MentorCareer career1 = MentorCareer.builder()
                .mentor(mentor)
                .company("네이버")
                .position("백엔드 개발자")
                .description("검색 서비스 개발")
                .startDate(LocalDate.of(2022, 3, 1))
                .endDate(null)
                .isCurrent(true)
                .displayOrder(1)
                .build();
        mentorCareerRepository.save(career1);

        MentorCareer career2 = MentorCareer.builder()
                .mentor(mentor)
                .company("카카오")
                .position("주니어 개발자")
                .description("메신저 서비스 개발")
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2022, 2, 28))
                .isCurrent(false)
                .displayOrder(2)
                .build();
        mentorCareerRepository.save(career2);

        mockMvc.perform(get("/mentors/{id}", mentor.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.company").value("네이버")) // 현재 회사
                .andExpect(jsonPath("$.data.career").isArray())
                .andExpect(jsonPath("$.data.career").value(hasSize(2)))
                .andExpect(jsonPath("$.data.career[0]").value("네이버 - 백엔드 개발자 (2022.03 ~ 현재)"))
                .andExpect(jsonPath("$.data.career[1]").value("카카오 - 주니어 개발자 (2020.01 ~ 2022.02)"))
                .andDo(result -> {
                    System.out.println("\n=== 멘토 상세 정보 (경력 포함) 응답 ===");
                    String jsonResponse = result.getResponse().getContentAsString();
                    System.out.println(jsonResponse);
                    System.out.println("==================================\n");
                });
    }

    @Test
    @DisplayName("멘토 상세 조회 - 피드백 정보 포함")
    void getMentorById_WithFeedback() throws Exception {
        // 추가 리뷰어 생성
        User reviewer2 = User.builder()
                .name("리뷰어2")
                .email("reviewer2@test.com")
                .role(Role.MENTEE)
                .provider("google")
                .providerId("reviewer2123")
                .build();
        userRepository.save(reviewer2);

        // 피드백 데이터 추가
        MentorFeedback feedback1 = MentorFeedback.builder()
                .mentor(mentor)
                .user(reviewUser)
                .rating(5)
                .comment("정말 도움이 많이 되었습니다. 친절하고 전문적이에요!")
                .isVisible(true)
                .build();
        mentorFeedbackRepository.save(feedback1);

        MentorFeedback feedback2 = MentorFeedback.builder()
                .mentor(mentor)
                .user(reviewer2)
                .rating(4)
                .comment("설명이 명확하고 이해하기 쉬웠습니다.")
                .isVisible(true)
                .build();
        mentorFeedbackRepository.save(feedback2);

        // 비공개 피드백 (응답에 포함되지 않아야 함)
        MentorFeedback hiddenFeedback = MentorFeedback.builder()
                .mentor(mentor)
                .user(reviewer2)
                .rating(3)
                .comment("비공개 피드백")
                .isVisible(false)
                .build();
        mentorFeedbackRepository.save(hiddenFeedback);

        mockMvc.perform(get("/mentors/{id}", mentor.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.feedback.rating").value(4.5)) // (5+4)/2 = 4.5
                .andExpect(jsonPath("$.data.feedback.count").value(2))
                .andExpect(jsonPath("$.data.feedback.comments").isArray())
                .andExpect(jsonPath("$.data.feedback.comments").value(hasSize(2)))
                .andExpect(jsonPath("$.data.feedback.comments[0].user").value("리뷰어"))
                .andExpect(jsonPath("$.data.feedback.comments[0].rating").value(5))
                .andExpect(jsonPath("$.data.feedback.comments[0].comment").value("정말 도움이 많이 되었습니다. 친절하고 전문적이에요!"))
                .andExpect(jsonPath("$.data.feedback.comments[0].date").exists())
                .andExpect(jsonPath("$.data.feedback.comments[1].user").value("리뷰어2"))
                .andExpect(jsonPath("$.data.feedback.comments[1].rating").value(4))
                .andDo(result -> {
                    System.out.println("\n=== 멘토 상세 정보 (피드백 포함) 응답 ===");
                    String jsonResponse = result.getResponse().getContentAsString();
                    System.out.println(jsonResponse);
                    System.out.println("==================================\n");
                });
    }

    @Test
    @DisplayName("멘토 상세 조회 - 경력과 피드백 모두 포함")
    void getMentorById_WithCareerAndFeedback() throws Exception {
        // 경력 정보 추가
        MentorCareer currentCareer = MentorCareer.builder()
                .mentor(mentor)
                .company("구글코리아")
                .position("시니어 소프트웨어 엔지니어")
                .description("검색 알고리즘 개발")
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(null)
                .isCurrent(true)
                .displayOrder(1)
                .build();
        mentorCareerRepository.save(currentCareer);

        MentorCareer pastCareer = MentorCareer.builder()
                .mentor(mentor)
                .company("라인")
                .position("백엔드 개발자")
                .description("메신저 플랫폼 개발")
                .startDate(LocalDate.of(2021, 6, 1))
                .endDate(LocalDate.of(2022, 12, 31))
                .isCurrent(false)
                .displayOrder(2)
                .build();
        mentorCareerRepository.save(pastCareer);

        // 피드백 정보 추가
        MentorFeedback feedback = MentorFeedback.builder()
                .mentor(mentor)
                .user(reviewUser)
                .rating(5)
                .comment("최고의 멘토입니다!")
                .isVisible(true)
                .build();
        mentorFeedbackRepository.save(feedback);

        mockMvc.perform(get("/mentors/{id}", mentor.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.company").value("구글코리아"))
                .andExpect(jsonPath("$.data.career").value(hasSize(2)))
                .andExpect(jsonPath("$.data.career[0]").value("구글코리아 - 시니어 소프트웨어 엔지니어 (2023.01 ~ 현재)"))
                .andExpect(jsonPath("$.data.career[1]").value("라인 - 백엔드 개발자 (2021.06 ~ 2022.12)"))
                .andExpect(jsonPath("$.data.feedback.rating").value(5.0))
                .andExpect(jsonPath("$.data.feedback.count").value(1))
                .andExpect(jsonPath("$.data.feedback.comments").value(hasSize(1)))
                .andExpect(jsonPath("$.data.feedback.comments[0].comment").value("최고의 멘토입니다!"))
                .andDo(result -> {
                    System.out.println("\n=== 멘토 상세 정보 (전체 데이터) 응답 ===");
                    String jsonResponse = result.getResponse().getContentAsString();
                    System.out.println(jsonResponse);
                    System.out.println("=====================================\n");
                });
    }

    @Test
    @DisplayName("멘토 상세 조회 - MentoringType별 meetingType 변환 테스트")
    void getMentorById_MeetingTypeConversion() throws Exception {
        // ONLINE 멘토
        User onlineUser = User.builder()
                .name("온라인멘토")
                .email("online@test.com")
                .role(Role.MENTOR)
                .provider("google")
                .providerId("online123")
                .region("서울시")
                .build();
        userRepository.save(onlineUser);

        Mentor onlineMentor = Mentor.builder()
                .id(onlineUser.getId())
                .user(onlineUser)
                .position("온라인 강사")
                .description("온라인 전문가")
                .mentoringType(MentoringType.ONLINE)
                .isVerified(true)
                .build();
        mentorRepository.save(onlineMentor);

        // OFFLINE 멘토
        User offlineUser = User.builder()
                .name("오프라인멘토")
                .email("offline@test.com")
                .role(Role.MENTOR)
                .provider("google")
                .providerId("offline123")
                .region("서울시")
                .build();
        userRepository.save(offlineUser);

        Mentor offlineMentor = Mentor.builder()
                .id(offlineUser.getId())
                .user(offlineUser)
                .position("오프라인 강사")
                .description("오프라인 전문가")
                .mentoringType(MentoringType.OFFLINE)
                .isVerified(true)
                .build();
        mentorRepository.save(offlineMentor);

        // ONLINE 테스트
        mockMvc.perform(get("/mentors/{id}", onlineMentor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.meetingType").value("online"));

        // OFFLINE 테스트
        mockMvc.perform(get("/mentors/{id}", offlineMentor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.meetingType").value("offline"));

        // BOTH 테스트 (기존 멘토)
        mockMvc.perform(get("/mentors/{id}", mentor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.meetingType").value("both"));
    }

    @Test
    @DisplayName("존재하지 않는 멘토 조회 시 404 에러")
    void getMentorById_NotFound() throws Exception {
        mockMvc.perform(get("/mentors/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("경력 표시 순서 및 날짜 정렬 테스트")
    void getMentorById_CareerSortingTest() throws Exception {
        // displayOrder와 날짜 순서가 다른 경력들 추가
        MentorCareer career3 = MentorCareer.builder()
                .mentor(mentor)
                .company("회사C")
                .position("포지션C")
                .startDate(LocalDate.of(2023, 1, 1))
                .displayOrder(3)
                .isCurrent(false)
                .build();
        mentorCareerRepository.save(career3);

        MentorCareer career1 = MentorCareer.builder()
                .mentor(mentor)
                .company("회사A")
                .position("포지션A")
                .startDate(LocalDate.of(2024, 1, 1))
                .displayOrder(1)
                .isCurrent(true)
                .build();
        mentorCareerRepository.save(career1);

        MentorCareer career2 = MentorCareer.builder()
                .mentor(mentor)
                .company("회사B")
                .position("포지션B")
                .startDate(LocalDate.of(2022, 1, 1))
                .displayOrder(2)
                .isCurrent(false)
                .build();
        mentorCareerRepository.save(career2);

        mockMvc.perform(get("/mentors/{id}", mentor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.career").value(hasSize(3)))
                .andExpect(jsonPath("$.data.career[0]").value("회사A - 포지션A (2024.01 ~ 현재)")) // displayOrder 1
                .andExpect(jsonPath("$.data.career[1]").value("회사B - 포지션B (2022.01 ~ 현재)")) // displayOrder 2
                .andExpect(jsonPath("$.data.career[2]").value("회사C - 포지션C (2023.01 ~ 현재)")) // displayOrder 3
                .andExpect(jsonPath("$.data.company").value("회사A")) // 현재 회사는 isCurrent=true인 회사
                .andDo(result -> {
                    System.out.println("\n=== 경력 정렬 테스트 응답 ===");
                    String jsonResponse = result.getResponse().getContentAsString();
                    System.out.println(jsonResponse);
                    System.out.println("==========================\n");
                });
    }
}