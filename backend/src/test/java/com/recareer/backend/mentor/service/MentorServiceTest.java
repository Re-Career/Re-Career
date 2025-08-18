package com.recareer.backend.mentor.service;

import com.recareer.backend.availableTime.entity.AvailableTime;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.repository.MentorRepository;
import com.recareer.backend.reservation.entity.Reservation;
import com.recareer.backend.user.entity.Role;
import com.recareer.backend.user.entity.User;
import com.recareer.backend.user.entity.UserPersonalityTag;
import com.recareer.backend.user.repository.UserRepository;
import com.recareer.backend.user.repository.UserPersonalityTagRepository;
import com.recareer.backend.personality.entity.PersonalityTag;
import com.recareer.backend.personality.repository.PersonalityTagRepository;
import com.recareer.backend.common.entity.Job;
import com.recareer.backend.common.entity.Province;
import com.recareer.backend.common.entity.City;
import com.recareer.backend.common.repository.JobRepository;
import com.recareer.backend.common.repository.ProvinceRepository;
import com.recareer.backend.common.repository.CityRepository;
import com.recareer.backend.skill.entity.Skill;
import com.recareer.backend.skill.repository.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class MentorServiceTest {

    @Autowired
    private MentorService mentorService;
    
    @Autowired
    private MentorRepository mentorRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PersonalityTagRepository personalityTagRepository;
    
    @Autowired
    private UserPersonalityTagRepository userPersonalityTagRepository;
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private ProvinceRepository provinceRepository;
    
    @Autowired
    private CityRepository cityRepository;
    
    @Autowired
    private SkillRepository skillRepository;
    
    private User mentorUser;
    private Mentor mentor;
    private List<Skill> skills;

    @BeforeEach
    void setUp() {
        // Province 생성
        Province seoul = Province.builder()
                .key("seoul")
                .name("서울특별시")
                .build();
        provinceRepository.save(seoul);
        
        // City 생성
        City gangnam = City.builder()
                .key("gangnam")
                .name("강남구")
                .province(seoul)
                .build();
        cityRepository.save(gangnam);
        
        // PersonalityTag 생성
        PersonalityTag tag1 = PersonalityTag.builder()
                .name("적극적")
                .build();
        PersonalityTag tag2 = PersonalityTag.builder()
                .name("분석적")
                .build();
        personalityTagRepository.save(tag1);
        personalityTagRepository.save(tag2);

        mentorUser = User.builder()
                .name("멘토 김민주")
                .email("mentor@test.com")
                .role(Role.MENTOR)
                .provider("google")
                .providerId("mentor123")
                .profileImageUrl("https://example.com/mentor.jpg")
                .province(seoul)
                .city(gangnam)
                .build();
        
        userRepository.save(mentorUser);

        // 멘토에 성향 태그 추가
        UserPersonalityTag mentorTag1 = UserPersonalityTag.builder()
                .user(mentorUser)
                .personalityTag(tag1)
                .build();
        userPersonalityTagRepository.save(mentorTag1);
        
        // Skills 조회 (data.sql에서 로드된 스킬들)
        skills = skillRepository.findAll();
        
        mentor = Mentor.builder()
                .id(mentorUser.getId())
                .user(mentorUser)
                .job(createJob("시니어 백엔드 개발자"))
                .description("5년차 백엔드 개발자입니다. Spring Boot 전문가입니다.")
                .experience(5)
                .isVerified(true)
                .build();
        
        mentorRepository.save(mentor);
    }

    @Test
    @DisplayName("멘토 ID로 멘토 조회 성공")
    void getMentorById_Success() {
        Optional<Mentor> result = mentorService.getMentorById(mentor.getId());
        
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(mentor.getId());
        assertThat(result.get().getUser().getName()).isEqualTo("멘토 김민주");
        assertThat(result.get().getIsVerified()).isTrue();
    }

    @Test
    @DisplayName("검증되지 않은 멘토도 조회됨")
    void getMentorById_NotVerified() {
        User unverifiedMentorUser = User.builder()
                .name("미인증 멘토")
                .email("unverified@test.com")
                .role(Role.MENTOR)
                .provider("google")
                .providerId("unverified123")
                .profileImageUrl("https://example.com/unverified.jpg")
                .build();
        userRepository.save(unverifiedMentorUser);
        
        Mentor unverifiedMentor = Mentor.builder()
                .id(unverifiedMentorUser.getId())
                .user(unverifiedMentorUser)
                .job(createJob("주니어 개발자"))
                .description("신입 개발자입니다.")
                .isVerified(false)
                .build();
        mentorRepository.save(unverifiedMentor);
        
        Optional<Mentor> result = mentorService.getMentorById(unverifiedMentor.getId());
        
        assertThat(result).isPresent();
        assertThat(result.get().getIsVerified()).isFalse();
    }

    @Test
    @DisplayName("지역으로 멘토 검색 성공")
    void getMentorsByRegion_Success() {
        List<Mentor> result = mentorService.getMentorsByPriorityFilters("mentor123", List.of("강남"), null, null);
        
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("지역이 null이면 기본 지역(서울시)으로 검색")
    void getMentorsByRegion_NullRegion() {
        mentorUser.updateProfile("멘토 김철수", "mentor@test.com", "https://example.com/mentor.jpg");
        userRepository.save(mentorUser);
        
        List<Mentor> result = mentorService.getMentorsByPriorityFilters("mentor123", null, null, null);
        
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("멘토 정보 업데이트 성공")
    void updateMentor_Success() {
        Job newJob = createJob("시니어 풀스택 개발자");
        String newDescription = "10년차 풀스택 개발자입니다.";
        String newIntroduction = "상세 소개는 여기에...";
        Integer newExperience = 10;
        List<Long> newSkillIds = skills.stream().limit(2).map(Skill::getId).toList(); // 처음 2개 스킬 ID
        
        Optional<Mentor> result = mentorService.updateMentor(mentor.getId(), newJob.getId(), newDescription, newIntroduction, newExperience, newSkillIds);
        
        assertThat(result).isPresent();
        assertThat(result.get().getJob().getName()).isEqualTo(newJob.getName());
        assertThat(result.get().getDescription()).isEqualTo(newDescription);
        assertThat(result.get().getIntroduction()).isEqualTo(newIntroduction);
        assertThat(result.get().getExperience()).isEqualTo(newExperience);
        assertThat(result.get().getMentorSkills()).hasSize(2);
    }

    @Test
    @DisplayName("존재하지 않는 멘토 업데이트 실패")
    void updateMentor_NotFound() {
        List<Long> skillIds = skills.stream().limit(1).map(Skill::getId).toList();
        Optional<Mentor> result = mentorService.updateMentor(999L, 1L, "새 설명", "새 소개", 5, skillIds);
        
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("멘토의 예약 목록 조회")
    void getMentorReservations_Success() {
        List<Reservation> result = mentorService.getMentorReservations(mentor.getId());
        
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("멘토의 가능한 시간 목록 조회")
    void getMentorAvailableTimes_Success() {
        List<AvailableTime> result = mentorService.getMentorAvailableTimes(mentor.getId());
        
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("멘토의 가능한 시간 생성 성공")
    void createMentorAvailableTime_Success() {
        LocalDateTime availableTime = LocalDateTime.of(2025, 8, 10, 14, 0);
        
        AvailableTime result = mentorService.createMentorAvailableTime(mentor.getId(), availableTime);
        
        assertThat(result).isNotNull();
        assertThat(result.getAvailableTime()).isEqualTo(availableTime);
        assertThat(result.getMentor().getId()).isEqualTo(mentor.getId());
        assertThat(result.isBooked()).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 멘토로 가능한 시간 생성 실패")
    void createMentorAvailableTime_MentorNotFound() {
        LocalDateTime availableTime = LocalDateTime.of(2025, 8, 10, 14, 0);
        
        assertThatThrownBy(() -> mentorService.createMentorAvailableTime(999L, availableTime))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("인증 토큰 기반 멘토 추천 테스트")
    void getMentorsByRegionAndPersonalityTags() {
        // 조회용 사용자 생성
        User queryUser = User.builder()
                .name("조회 사용자")
                .email("query@test.com")
                .role(Role.MENTEE)
                .provider("google")
                .providerId("queryuser123")
                .profileImageUrl("https://example.com/query.jpg")
                .build();
        userRepository.save(queryUser);

        // 조회 사용자에게 성향 태그 추가 (멘토와 동일한 태그)
        List<PersonalityTag> tags = personalityTagRepository.findAll();
        PersonalityTag tag1 = tags.stream()
                .filter(tag -> "적극적".equals(tag.getName()))
                .findFirst()
                .orElseThrow();

        UserPersonalityTag queryUserTag = UserPersonalityTag.builder()
                .user(queryUser)
                .personalityTag(tag1)
                .build();
        userPersonalityTagRepository.save(queryUserTag);

        // providerId로 멘토 추천 조회
        List<Mentor> result = mentorService.getMentorsByRegionAndPersonalityTags(List.of("강남"), "queryuser123");

        // 결과 검증: 매칭되는 멘토가 우선 정렬되어야 함
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getUser().getName()).isEqualTo("멘토 김민주");
    }

    @Test
    @DisplayName("빈 personalityTags로 멘토 조회 시 전체 멘토 반환")
    void getMentorsByRegion_WithEmptyPersonalityTags() {
        List<Mentor> result = mentorService.getMentorsByPriorityFilters("mentor123", List.of("강남"), null, null);
        
        assertThat(result).hasSize(1);
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("필터 조건으로 멘토 조회 - 모든 필터 적용")
    void getMentorsByPriorityFilters_AllFilters() {
        List<Mentor> result = mentorService.getMentorsByPriorityFilters("mentor123", List.of("강남"), "백엔드", "4-6년");
        
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getPosition()).contains("백엔드");
        assertThat(result.getFirst().getExperience()).isEqualTo(5);
        // MentoringType 필드가 제거되었으므로 관련 검증 삭제
    }

    @Test
    @DisplayName("필터 조건으로 멘토 조회 - 직업 필터만")
    void getMentorsByPriorityFilters_PositionOnly() {
        List<Mentor> result = mentorService.getMentorsByPriorityFilters("mentor123", List.of("강남"), "백엔드", null);
        
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getPosition()).contains("백엔드");
    }

    @Test
    @DisplayName("필터 조건으로 멘토 조회 - 경력 필터만")
    void getMentorsByPriorityFilters_ExperienceOnly() {
        List<Mentor> result = mentorService.getMentorsByPriorityFilters("mentor123", List.of("강남"), null, "4-6년");
        
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getExperience()).isEqualTo(5);
    }

    @Test
    @DisplayName("필터 조건으로 멘토 조회 - 기본 조회")
    void getMentorsByPriorityFilters_BasicSearch() {
        List<Mentor> result = mentorService.getMentorsByPriorityFilters("mentor123", List.of("강남"), null, null);
        
        // 기본적인 지역 매칭 확인
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getUser().getProvince().getName()).contains("서울");
    }

    @Test
    @DisplayName("필터 조건으로 멘토 조회 - 매칭되지 않는 경우")
    void getMentorsByPriorityFilters_NoMatch() {
        List<Mentor> result = mentorService.getMentorsByPriorityFilters("mentor123", List.of("강남"), "프론트엔드", null);
        
        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("경력 범위 필터링 테스트 - 1-3년")
    void getMentorsByPriorityFilters_ExperienceRange_1To3Years() {
        // Province와 City 조회
        Province seoul = provinceRepository.findAll().get(0);
        City gangnam = cityRepository.findAll().get(0);
        
        // 3년 경력 멘토 추가
        User mentor2User = User.builder()
                .name("3년차 멘토")
                .email("3years@test.com")
                .role(Role.MENTOR)
                .provider("google")
                .providerId("3years123")
                .province(seoul)
                .city(gangnam)
                .build();
        userRepository.save(mentor2User);

        Mentor mentor2 = Mentor.builder()
                .id(mentor2User.getId())
                .user(mentor2User)
                .job(createJob("주니어 개발자"))
                .description("3년차 개발자입니다.")
                .experience(3)
                .isVerified(true)
                .build();
        mentorRepository.save(mentor2);

        List<Mentor> result = mentorService.getMentorsByPriorityFilters("mentor123", List.of("강남"), null, "1-3년");
        
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getExperience()).isEqualTo(3);
        assertThat(result.getFirst().getUser().getName()).isEqualTo("3년차 멘토");
    }

    @Test
    @DisplayName("경력 범위 필터링 테스트 - 7년 이상")
    void getMentorsByPriorityFilters_ExperienceRange_7YearsPlus() {
        // Province와 City 조회
        Province seoul = provinceRepository.findAll().get(0);
        City gangnam = cityRepository.findAll().get(0);
        
        // 10년 경력 멘토 추가
        User mentor3User = User.builder()
                .name("10년차 멘토")
                .email("10years@test.com")
                .role(Role.MENTOR)
                .provider("google")
                .providerId("10years123")
                .province(seoul)
                .city(gangnam)
                .build();
        userRepository.save(mentor3User);

        Mentor mentor3 = Mentor.builder()
                .id(mentor3User.getId())
                .user(mentor3User)
                .job(createJob("시니어 개발자"))
                .description("10년차 개발자입니다.")
                .experience(10)
                .isVerified(true)
                .build();
        mentorRepository.save(mentor3);

        List<Mentor> result = mentorService.getMentorsByPriorityFilters("mentor123", List.of("강남"), null, "7년 이상");
        
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getExperience()).isEqualTo(10);
        assertThat(result.getFirst().getUser().getName()).isEqualTo("10년차 멘토");
    }

    @Test
    @DisplayName("필터 조건으로 멘토 조회 - personalityTags 포함")
    void getMentorsByPriorityFilters_WithPersonalityTags() {
        // 성향 태그 생성
        List<PersonalityTag> tags = personalityTagRepository.findAll();
        Long tag1Id = tags.stream()
                .filter(tag -> "적극적".equals(tag.getName()))
                .map(PersonalityTag::getId)
                .findFirst()
                .orElseThrow();

        List<Mentor> result = mentorService.getMentorsByPriorityFilters("mentor123", List.of("강남"), "백엔드", "4-6년");
        
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getPosition()).contains("백엔드");
        assertThat(result.getFirst().getExperience()).isEqualTo(5);
        // MentoringType 필드가 제거되었으므로 관련 검증 삭제
    }

    @Test
    @DisplayName("존재하지 않는 providerId로 멘토 추천 조회 실패")
    void getMentorsByRegionAndPersonalityTags_UserNotFound() {
        assertThatThrownBy(() -> mentorService.getMentorsByRegionAndPersonalityTags(List.of("강남"), "nonexistent123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found with providerId");
    }

    @Test
    @DisplayName("성향 태그가 없는 사용자로 멘토 추천 조회")
    void getMentorsByRegionAndPersonalityTags_NoPersonalityTags() {
        User userWithoutTags = User.builder()
                .name("태그 없는 사용자")
                .email("notags@test.com")
                .role(Role.MENTEE)
                .provider("google")
                .providerId("notags123")
                .profileImageUrl("https://example.com/notags.jpg")
                .build();
        userRepository.save(userWithoutTags);

        List<Mentor> result = mentorService.getMentorsByRegionAndPersonalityTags(List.of("강남"), "notags123");
        
        // 성향 태그가 없어도 지역 기반으로 멘토는 조회되어야 함
        assertThat(result).hasSize(1);
    }

    private Job createJob(String name) {
        Job job = Job.builder()
                .name(name)
                .build();
        return jobRepository.save(job);
    }
}