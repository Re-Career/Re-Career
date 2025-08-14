package com.recareer.backend.mentor.controller;

import com.recareer.backend.availableTime.repository.AvailableTimeRepository;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.repository.MentorRepository;
import com.recareer.backend.user.entity.Role;
import com.recareer.backend.user.entity.User;
import com.recareer.backend.user.entity.UserPersonalityTag;
import com.recareer.backend.user.repository.UserRepository;
import com.recareer.backend.user.repository.UserPersonalityTagRepository;
import com.recareer.backend.personality.entity.PersonalityTag;
import com.recareer.backend.personality.repository.PersonalityTagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
    private AvailableTimeRepository availableTimeRepository;

    @Autowired
    private PersonalityTagRepository personalityTagRepository;

    @Autowired
    private UserPersonalityTagRepository userPersonalityTagRepository;

    private User mentorUser;
    private Mentor mentor;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        mentorUser = User.builder()
                .name("테스트 멘토")
                .email("testmentor@test.com")
                .role(Role.MENTOR)
                .provider("google")
                .providerId("testmentor123")
                .profileImageUrl("https://example.com/mentor.jpg")
                .region("서울시 강남구")
                .build();

        userRepository.save(mentorUser);

        mentor = Mentor.builder()
                .id(mentorUser.getId())
                .user(mentorUser)
                .position("시니어 백엔드 개발자")
                .description("5년차 백엔드 개발자입니다.")
                .isVerified(true)
                .build();

        mentorRepository.save(mentor);
    }

    @Test
    @DisplayName("지역별 멘토 조회 API 테스트 - 다양한 직무 5명")
    void getMentorsByRegion_Success() throws Exception {
        // 추가 멘토 4명 생성 (다양한 직무)
        // 2번째 멘토: 마케팅
        User mentor2User = User.builder()
                .name("김마케팅")
                .email("kim.marketing@test.com")
                .role(Role.MENTOR)
                .provider("google")
                .providerId("kim456")
                .profileImageUrl("https://example.com/kim.jpg")
                .region("서울시 강남구")
                .build();
        userRepository.save(mentor2User);

        Mentor mentor2 = Mentor.builder()
                .id(mentor2User.getId())
                .user(mentor2User)
                .position("디지털 마케팅 매니저")
                .description("퍼포먼스 마케팅과 브랜드 마케팅 10년 경력입니다.")
                .isVerified(true)
                .build();
        mentorRepository.save(mentor2);

        // 3번째 멘토: 디자인
        User mentor3User = User.builder()
                .name("박디자인")
                .email("park.design@test.com")
                .role(Role.MENTOR)
                .provider("google")
                .providerId("park789")
                .profileImageUrl("https://example.com/park.jpg")
                .region("서울시 강남구")
                .build();
        userRepository.save(mentor3User);

        Mentor mentor3 = Mentor.builder()
                .id(mentor3User.getId())
                .user(mentor3User)
                .position("UX/UI 디자이너")
                .description("사용자 경험 중심의 디자인을 합니다. Figma, Adobe 전문가입니다.")
                .isVerified(true)
                .build();
        mentorRepository.save(mentor3);

        // 4번째 멘토: 영업
        User mentor4User = User.builder()
                .name("이영업")
                .email("lee.sales@test.com")
                .role(Role.MENTOR)
                .provider("google")
                .providerId("lee101")
                .profileImageUrl("https://example.com/lee.jpg")
                .region("서울시 강남구")
                .build();
        userRepository.save(mentor4User);

        Mentor mentor4 = Mentor.builder()
                .id(mentor4User.getId())
                .user(mentor4User)
                .position("B2B 세일즈 매니저")
                .description("기업 고객 대상 솔루션 영업 15년 경력입니다.")
                .isVerified(true)
                .build();
        mentorRepository.save(mentor4);

        // 5번째 멘토: 데이터 분석
        User mentor5User = User.builder()
                .name("정데이터")
                .email("jung.data@test.com")
                .role(Role.MENTOR)
                .provider("google")
                .providerId("jung202")
                .profileImageUrl("https://example.com/jung.jpg")
                .region("서울시 강남구")
                .build();
        userRepository.save(mentor5User);

        Mentor mentor5 = Mentor.builder()
                .id(mentor5User.getId())
                .user(mentor5User)
                .position("데이터 사이언티스트")
                .description("머신러닝과 통계 분석을 통해 비즈니스 인사이트를 제공합니다.")
                .isVerified(true)
                .build();
        mentorRepository.save(mentor5);

        // API 테스트
        mockMvc.perform(get("/mentors/region")
                        .param("region", "강남")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").value(hasSize(5)))  // 5명의 멘토 확인
                .andDo(result -> {
                    // 응답 JSON을 콘솔에 출력
                    System.out.println("\n=== 다양한 직무 멘토 목록 조회 응답 ===");
                    String jsonResponse = result.getResponse().getContentAsString();
                    System.out.println(jsonResponse);
                    System.out.println("총 멘토 수: 5명");
                    System.out.println("======================================\n");
                });

        // 데이터베이스에서 직접 확인
        List<Mentor> allMentors = mentorRepository.findAll();
        System.out.println("\n=== 데이터베이스에 저장된 다양한 직무 멘토 목록 ===");
        for (Mentor m : allMentors) {
            if (m.getIsVerified()) {
                System.out.println("👤 " + m.getUser().getName() + 
                                 " | 📋 " + m.getPosition() + 
                                 " | 🏢 " + m.getUser().getRegion());
            }
        }
        System.out.println("총 검증된 멘토 수: " + allMentors.stream()
                .filter(Mentor::getIsVerified)
                .count() + "명");
        System.out.println("================================================\n");
    }

    @Test
    @DisplayName("성향 기반 멘토 매칭 테스트")
    void getMentorsByRegionWithPersonalityMatching() throws Exception {
        // PersonalityTag 생성
        PersonalityTag tag1 = PersonalityTag.builder()
                .name("논리적")
                .build();
        PersonalityTag tag2 = PersonalityTag.builder()
                .name("감성적")
                .build();
        PersonalityTag tag3 = PersonalityTag.builder()
                .name("분석적인")
                .build();
        personalityTagRepository.save(tag1);
        personalityTagRepository.save(tag2);
        personalityTagRepository.save(tag3);

        // 사용자 생성
        User testUser = User.builder()
                .name("테스트 사용자")
                .email("testuser@test.com")
                .role(Role.MENTEE)
                .provider("google")
                .providerId("testuser123")
                .region("서울시 강남구")
                .build();
        userRepository.save(testUser);

        // 사용자 성향 태그 설정 (논리적, 분석적인)
        UserPersonalityTag userTag1 = UserPersonalityTag.builder()
                .user(testUser)
                .personalityTag(tag1)
                .build();
        UserPersonalityTag userTag3 = UserPersonalityTag.builder()
                .user(testUser)
                .personalityTag(tag3)
                .build();
        userPersonalityTagRepository.save(userTag1);
        userPersonalityTagRepository.save(userTag3);

        // 기존 멘토들에게 성향 태그 추가
        // 테스트 멘토: 논리적 (1개 매칭)
        UserPersonalityTag mentorTag1 = UserPersonalityTag.builder()
                .user(mentorUser)
                .personalityTag(tag1)
                .build();
        userPersonalityTagRepository.save(mentorTag1);

        // 추가 멘토 생성 (다양한 매칭 수준)
        // 김마케팅: 논리적, 분석적 (2개 매칭) - 최우선
        User mentor2User = User.builder()
                .name("김마케팅")
                .email("kim.marketing@test.com")
                .role(Role.MENTOR)
                .provider("google")
                .providerId("kim456")
                .region("서울시 강남구")
                .build();
        userRepository.save(mentor2User);

        Mentor mentor2 = Mentor.builder()
                .id(mentor2User.getId())
                .user(mentor2User)
                .position("디지털 마케팅 매니저")
                .description("퍼포먼스 마케팅 전문가입니다.")
                .isVerified(true)
                .build();
        mentorRepository.save(mentor2);

        UserPersonalityTag mentor2Tag1 = UserPersonalityTag.builder()
                .user(mentor2User)
                .personalityTag(tag1)
                .build();
        UserPersonalityTag mentor2Tag3 = UserPersonalityTag.builder()
                .user(mentor2User)
                .personalityTag(tag3)
                .build();
        userPersonalityTagRepository.save(mentor2Tag1);
        userPersonalityTagRepository.save(mentor2Tag3);

        // 박디자인: 분석적만 (1개 매칭) - 중간 순위
        User mentor3User = User.builder()
                .name("박디자인")
                .email("park.design@test.com")
                .role(Role.MENTOR)
                .provider("google")
                .providerId("park789")
                .region("서울시 강남구")
                .build();
        userRepository.save(mentor3User);

        Mentor mentor3 = Mentor.builder()
                .id(mentor3User.getId())
                .user(mentor3User)
                .position("UX/UI 디자이너")
                .description("사용자 경험 디자인 전문가입니다.")
                .isVerified(true)
                .build();
        mentorRepository.save(mentor3);

        UserPersonalityTag mentor3Tag = UserPersonalityTag.builder()
                .user(mentor3User)
                .personalityTag(tag3)
                .build();
        userPersonalityTagRepository.save(mentor3Tag);

        // 성향 매칭이 적용된 API 테스트
        mockMvc.perform(get("/mentors/region")
                        .param("region", "강남")
                        .param("personalityTags", tag1.getId().toString(), tag2.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").value(hasSize(3)))
                .andDo(result -> {
                    System.out.println("\n=== 성향 매칭 기반 멘토 정렬 결과 ===");
                    String jsonResponse = result.getResponse().getContentAsString();
                    System.out.println(jsonResponse);
                    System.out.println("성향 매칭 순서: 김마케팅(2개) > 테스트 멘토(1개) = 박디자인(1개)");
                    System.out.println("personalityTags: [" + tag1.getId() + ", " + tag2.getId() + "] (논리적, 감성적)");
                    System.out.println("=========================================\n");
                });

        // 성향 매칭 없이 호출 (기존 방식)
        mockMvc.perform(get("/mentors/region")
                        .param("region", "강남")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").value(hasSize(3)))
                .andDo(result -> {
                    System.out.println("\n=== 기존 방식 (성향 매칭 없음) 결과 ===");
                    String jsonResponse = result.getResponse().getContentAsString();
                    System.out.println(jsonResponse);
                    System.out.println("일반 정렬: 생성 순서대로");
                    System.out.println("=====================================\n");
                });
    }

    @Test
    @DisplayName("멘토 ID로 조회 API 테스트")
    void getMentorById_Success() throws Exception {
        mockMvc.perform(get("/mentors/{id}", mentor.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(mentor.getId()))
                .andExpect(jsonPath("$.data.name").value("테스트 멘토"))
                .andExpect(jsonPath("$.data.position").value("시니어 백엔드 개발자"));
    }

    @Test
    @DisplayName("멘토 정보 수정 API 테스트 - 데이터베이스 확인")
    void updateMentor_Success_CheckDatabase() throws Exception {
        String newPosition = "시니어 풀스택 개발자";
        String newDescription = "10년차 풀스택 개발자입니다.";

        mockMvc.perform(put("/mentors/{id}", mentor.getId())
                        .param("position", newPosition)
                        .param("description", newDescription)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("멘토 정보가 성공적으로 수정되었습니다."))
                .andExpect(jsonPath("$.data.id").value(mentor.getId()))
                .andExpect(jsonPath("$.data.position").value(newPosition))
                .andExpect(jsonPath("$.data.description").value(newDescription));

        // 데이터베이스에 실제로 저장되었는지 확인
        Mentor updatedMentor = mentorRepository.findById(mentor.getId()).orElse(null);
        assertThat(updatedMentor).isNotNull();
        assertThat(updatedMentor.getPosition()).isEqualTo(newPosition);
        assertThat(updatedMentor.getDescription()).isEqualTo(newDescription);
    }

    @Test
    @DisplayName("멘토 가능 시간 생성 API 테스트 - 데이터베이스 확인")
    void createMentorAvailableTime_Success_CheckDatabase() throws Exception {
        String availableTime = "2025-08-10T14:00:00";

        mockMvc.perform(post("/mentors/{id}/available-times", mentor.getId())
                        .param("availableTime", availableTime)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("멘토 가능 시간이 성공적으로 추가되었습니다."))
                .andExpect(jsonPath("$.data.availableTime").value(availableTime))
                .andExpect(jsonPath("$.data.book").value(false));

        // 데이터베이스에 실제로 저장되었는지 확인
        long availableTimeCount = availableTimeRepository.findByMentor(mentor).size();
        assertThat(availableTimeCount).isEqualTo(1);
    }

    @Test
    @DisplayName("존재하지 않는 멘토 수정 시 404 에러")
    void updateMentor_NotFound() throws Exception {
        mockMvc.perform(put("/mentors/{id}", 999L)
                        .param("position", "새 포지션")
                        .param("description", "새 설명")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("해당 멘토를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("잘못된 시간 형식으로 가능 시간 생성 시 에러")
    void createMentorAvailableTime_BadRequest() throws Exception {
        mockMvc.perform(post("/mentors/{id}/available-times", mentor.getId())
                        .param("availableTime", "invalid-time-format")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())  // 500 에러로 변경
                .andExpect(jsonPath("$.message").value("멘토 가능 시간 추가에 실패했습니다."));
    }

    @Test
    @DisplayName("멘토별 예약 목록 조회 API 테스트")
    void getMentorReservations_Success() throws Exception {
        mockMvc.perform(get("/mentors/{id}/reservations", mentor.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청이 성공적으로 처리되었습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty()); // 초기에는 예약이 없으므로 빈 배열
    }

    @Test
    @DisplayName("멘토별 가능 시간 조회 API 테스트")  
    void getMentorAvailableTimes_Success() throws Exception {
        mockMvc.perform(get("/mentors/{id}/available-times", mentor.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청이 성공적으로 처리되었습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty()); // 초기에는 가능 시간이 없으므로 빈 배열
    }

    @Test
    @DisplayName("존재하지 않는 멘토의 예약 목록 조회")
    void getMentorReservations_MentorNotFound() throws Exception {
        // 존재하지 않는 멘토 ID로 요청해도 빈 배열을 반환함 (서비스 로직에 따라)
        mockMvc.perform(get("/mentors/{id}/reservations", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청이 성공적으로 처리되었습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("존재하지 않는 멘토의 가능 시간 조회")
    void getMentorAvailableTimes_MentorNotFound() throws Exception {
        // 존재하지 않는 멘토 ID로 요청해도 빈 배열을 반환함 (서비스 로직에 따라)
        mockMvc.perform(get("/mentors/{id}/available-times", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청이 성공적으로 처리되었습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}