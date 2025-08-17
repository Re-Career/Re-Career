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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@Transactional
class MentorControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    private User mentorUser;
    private Mentor mentor;
    private Job testJob;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Job 생성
        testJob = Job.builder()
                .name("백엔드 개발자")
                .build();
        jobRepository.save(testJob);
        
        // User 생성 (Province/City 없이 간단하게)
        mentorUser = User.builder()
                .name("테스트 멘토")
                .email("testmentor@test.com")
                .role(Role.MENTOR)
                .provider("google")
                .providerId("testmentor123")
                .profileImageUrl("https://example.com/mentor.jpg")
                .build();
        userRepository.save(mentorUser);

        // Mentor 생성
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
    @DisplayName("기본 멘토 조회 테스트")
    void getMentors_Basic() throws Exception {
        mockMvc.perform(get("/mentors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("특정 멘토 ID로 조회 테스트")
    void getMentorById_Success() throws Exception {
        mockMvc.perform(get("/mentors/{id}", mentor.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(mentor.getId()))
                .andExpect(jsonPath("$.data.name").value("테스트 멘토"));
    }
}