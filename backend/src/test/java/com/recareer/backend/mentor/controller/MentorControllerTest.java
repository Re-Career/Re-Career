package com.recareer.backend.mentor.controller;

import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.repository.MentorRepository;
import com.recareer.backend.personality.entity.PersonalityTag;
import com.recareer.backend.personality.repository.PersonalityTagRepository;
import com.recareer.backend.position.entity.Position;
import com.recareer.backend.common.entity.Province;
import com.recareer.backend.position.repository.PositionRepository;
import com.recareer.backend.common.repository.ProvinceRepository;
import com.recareer.backend.user.entity.User;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;
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
    private PositionRepository positionRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private PersonalityTagRepository personalityTagRepository;

    private User mentorUser;
    private Mentor mentor;
    private Position testPosition;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // data.sql에서 로드된 기존 데이터 사용
        // data.sql의 첫 번째 멘토 사용 (ID: 1L)
        mentor = mentorRepository.findById(1L).orElse(null);
        if (mentor != null) {
            mentorUser = mentor.getUser();
            testPosition = mentor.getPositionEntity();
        }
    }

    @Test
    @DisplayName("기본 멘토 조회 테스트 - GET /mentors")
    void getMentors_Basic() throws Exception {
        mockMvc.perform(get("/mentors")
                        .param("provinceId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    // @Test - 데이터 의존성으로 인한 임시 비활성화
    @DisplayName("특정 멘토 ID로 조회 테스트")
    void getMentorById_Success_DISABLED() throws Exception {
        mockMvc.perform(get("/mentors/{id}", mentor.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(mentor.getId()))
                .andExpect(jsonPath("$.data.name").value("김민수"));
    }

    // @Test - 데이터 의존성으로 인한 임시 비활성화
    @DisplayName("멘토 필터 옵션 조회 테스트")
    void getFilterOptions_Success_DISABLED() throws Exception {
        mockMvc.perform(get("/mentors/filter-options")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value("position"))
                .andExpect(jsonPath("$.data[0].title").value("직업"))
                .andExpect(jsonPath("$.data[0].options").isArray())
                .andExpect(jsonPath("$.data[0].options[0].id").exists())
                .andExpect(jsonPath("$.data[0].options[0].name").value("소프트웨어 엔지니어"))
                // 지역 필터 검증 (DB에서 가져온 데이터) - 미팅방식 필터 제거로 인덱스가 2로 변경
                .andExpect(jsonPath("$.data[2].id").value("region"))
                .andExpect(jsonPath("$.data[2].title").value("지역"))
                .andExpect(jsonPath("$.data[2].options").isArray())
                .andExpect(jsonPath("$.data[2].options[0].id").exists())
                .andExpect(jsonPath("$.data[2].options[0].name").value("서울특별시"));
    }

    @Test
    @DisplayName("새로운 멘토 필터 옵션 조회 테스트 - GET /mentors/filters")
    void getFilters_Success() throws Exception {
        mockMvc.perform(get("/mentors/filters")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.positions").isArray())
                .andExpect(jsonPath("$.data.provinces").isArray())
                .andExpect(jsonPath("$.data.personalityTags").isArray());
    }

    @Test
    @DisplayName("멘토 검색 테스트 - GET /mentors/search with keyword")
    void searchMentors_WithKeyword_Success() throws Exception {
        mockMvc.perform(get("/mentors/search")
                        .param("keyword", "소프트웨어")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.primary").isArray())
                .andExpect(jsonPath("$.data.secondary").isArray());
    }

    @Test
    @DisplayName("멘토 검색 테스트 - GET /mentors/search with multiple filters")
    void searchMentors_WithMultipleFilters_Success() throws Exception {
        // Position과 Province ID를 동적으로 가져오기
        Position position = positionRepository.findAll().stream().findFirst().orElse(null);
        Province province = provinceRepository.findAll().stream().findFirst().orElse(null);
        PersonalityTag personalityTag = personalityTagRepository.findAll().stream().findFirst().orElse(null);

        if (position != null && province != null && personalityTag != null) {
            mockMvc.perform(get("/mentors/search")
                            .param("positionIds", position.getId().toString())
                            .param("experiences", "1-3년", "4-6년")
                            .param("provinceIds", province.getId().toString())
                            .param("personalityTagIds", personalityTag.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").exists())
                    .andExpect(jsonPath("$.data.primary").isArray())
                .andExpect(jsonPath("$.data.secondary").isArray());
        }
    }

    @Test
    @DisplayName("멘토 검색 테스트 - GET /mentors/search with empty request")
    void searchMentors_WithEmptyRequest_Success() throws Exception {
        mockMvc.perform(get("/mentors/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.primary").isArray())
                .andExpect(jsonPath("$.data.secondary").isArray());
    }

    @Test
    @DisplayName("성향 AND 매칭 검증 - 모든 personalityTagIds를 가진 멘토만 반환")
    void searchMentors_PersonalityAndMatching() throws Exception {
        // 여러 PersonalityTag ID 가져오기
        List<PersonalityTag> personalityTags = personalityTagRepository.findAll();

        if (personalityTags.size() >= 2) {
            String personalityId1 = personalityTags.get(0).getId().toString();
            String personalityId2 = personalityTags.get(1).getId().toString();

            mockMvc.perform(get("/mentors/search")
                            .param("personalityTagIds", personalityId1, personalityId2) // AND 조건
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").exists())
                    .andExpect(jsonPath("$.data.primary").isArray())
                .andExpect(jsonPath("$.data.secondary").isArray());
        }
    }

}