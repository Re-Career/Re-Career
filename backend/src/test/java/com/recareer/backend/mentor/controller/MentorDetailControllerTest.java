package com.recareer.backend.mentor.controller;

import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.entity.MentoringType;
import com.recareer.backend.mentor.repository.MentorRepository;
import com.recareer.backend.common.entity.Job;
import com.recareer.backend.common.repository.JobRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    private Mentor mentor;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Job 생성
        Job testJob = Job.builder()
                .name("시니어 백엔드 개발자")
                .build();
        jobRepository.save(testJob);
        
        // 멘토 사용자 생성
        User mentorUser = User.builder()
                .name("김멘토")
                .email("mentor@test.com")
                .role(Role.MENTOR)
                .provider("google")
                .providerId("mentor123")
                .profileImageUrl("https://example.com/mentor.jpg")
                .build();
        userRepository.save(mentorUser);

        // 멘토 생성
        mentor = Mentor.builder()
                .id(mentorUser.getId())
                .user(mentorUser)
                .job(testJob)
                .description("5년차 백엔드 개발자입니다.")
                .experience(5)
                .mentoringType(MentoringType.BOTH)
                .isVerified(true)
                .build();
        mentorRepository.save(mentor);
    }

    @Test
    @DisplayName("멘토 상세 조회 - 기본 정보")
    void getMentorById_Basic() throws Exception {
        mockMvc.perform(get("/mentors/{id}", mentor.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("멘토 상세 조회 - API 응답 구조")
    void getMentorById_Response() throws Exception {
        mockMvc.perform(get("/mentors/{id}", mentor.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(mentor.getId()))
                .andExpect(jsonPath("$.data.experience").value(5));
    }

    @Test
    @DisplayName("존재하지 않는 멘토 조회 시 404 에러")
    void getMentorById_NotFound() throws Exception {
        mockMvc.perform(get("/mentors/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}