package com.recareer.backend.mentor.controller;

import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.repository.MentorRepository;
import com.recareer.backend.position.entity.Position;
import com.recareer.backend.common.entity.Province;
import com.recareer.backend.common.entity.City;
import com.recareer.backend.position.repository.PositionRepository;
import com.recareer.backend.common.repository.ProvinceRepository;
import com.recareer.backend.common.repository.CityRepository;
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
    private PositionRepository positionRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private CityRepository cityRepository;

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
                .andExpect(jsonPath("$.data.name").value("김민수"));
    }

    @Test
    @DisplayName("멘토 필터 옵션 조회 테스트")
    void getFilterOptions_Success() throws Exception {
        mockMvc.perform(get("/mentors/filter-options")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value("job"))
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
}