package com.recareer.backend.mentor.service;

import com.recareer.backend.availableTime.entity.AvailableTime;
import com.recareer.backend.mentor.dto.MentorSummaryResponseDto;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.repository.MentorRepository;
import com.recareer.backend.reservation.entity.Reservation;
import com.recareer.backend.user.entity.Role;
import com.recareer.backend.user.entity.User;
import com.recareer.backend.user.repository.UserRepository;
import com.recareer.backend.position.entity.Position;
import com.recareer.backend.common.entity.Province;
import com.recareer.backend.common.entity.City;
import com.recareer.backend.position.repository.PositionRepository;
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
    private PositionRepository positionRepository;
    
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
        // 테스트 데이터 생성
        skills = skillRepository.findAll();
        
        // Province와 City 생성 (이미 존재하면 조회, 없으면 생성)
        Province province = provinceRepository.findByName("서울특별시").orElseGet(() -> 
            provinceRepository.save(Province.builder().key("seoul").name("서울특별시").build())
        );
        
        City city = cityRepository.findByName("강남구").orElseGet(() -> 
            cityRepository.save(City.builder().key("gangnam").name("강남구").province(province).build())
        );
        
        // Position 생성 (이미 존재하면 조회, 없으면 생성)
        Position position = positionRepository.findByName("소프트웨어 엔지니어").orElseGet(() -> 
            positionRepository.save(Position.builder().name("소프트웨어 엔지니어").build())
        );
        
        positionRepository.findByName("프로덕트 매니저").orElseGet(() -> 
            positionRepository.save(Position.builder().name("프로덕트 매니저").build())
        );
        
        // User 생성 (이미 존재하면 조회, 없으면 생성)
        mentorUser = userRepository.findByEmail("mentor@test.com").orElseGet(() -> 
            userRepository.save(User.builder()
                .name("김민수")
                .email("mentor@test.com")
                .role(Role.MENTOR)
                .provider("kakao")
                .providerId("test-provider-id")
                .province(province)
                .city(city)
                .build())
        );
        
        // Mentor 생성 (이미 존재하면 조회, 없으면 생성)
        mentor = mentorRepository.findByUser(mentorUser).orElseGet(() -> 
            mentorRepository.save(Mentor.builder()
                .user(mentorUser)
                .position(position)
                .description("5년차 백엔드 개발자입니다.")
                .introduction("Spring Boot와 Java를 주로 사용합니다.")
                .experience(5)
                .isVerified(true)
                .build())
        );
    }

    @Test
    @DisplayName("멘토 ID로 멘토 조회 성공")
    void getMentorById_Success() {
        Optional<Mentor> result = mentorService.getMentorById(mentor.getId());
        
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(mentor.getId());
        assertThat(result.get().getUser().getName()).isEqualTo("김민수");
        assertThat(result.get().getIsVerified()).isTrue();
    }

    @Test
    @DisplayName("검증되지 않은 멘토도 조회됨")
    void getMentorById_NotVerified() {
        // 검증되지 않은 멘토 생성
        User unverifiedUser = User.builder()
                .name("이지은")
                .email("unverified@test.com")
                .role(Role.MENTOR)
                .provider("kakao")
                .providerId("unverified-provider-id")
                .build();
        userRepository.save(unverifiedUser);
        
        Mentor unverifiedMentor = Mentor.builder()
                .user(unverifiedUser)
                .description("검증되지 않은 멘토입니다.")
                .isVerified(false)
                .build();
        mentorRepository.save(unverifiedMentor);
        
        Optional<Mentor> result = mentorService.getMentorById(unverifiedMentor.getId());
        
        assertThat(result).isPresent();
        assertThat(result.get().getUser().getName()).isEqualTo("이지은");
        assertThat(result.get().getIsVerified()).isFalse();
    }

    @Test
    @DisplayName("멘토 정보 업데이트 성공")
    void updateMentor_Success() {
        // 프로덕트 매니저 Position 조회
        Position pmPosition = positionRepository.findAll().stream()
                .filter(p -> "프로덕트 매니저".equals(p.getName()))
                .findFirst()
                .orElseThrow();
        
        String newDescription = "10년차 풀스택 개발자입니다.";
        String newIntroduction = "상세 소개는 여기에...";
        Integer newExperience = 10;
        List<Long> newSkillIds = List.of(); // 빈 스킬 리스트 사용
        
        Optional<Mentor> result = mentorService.updateMentor(mentor.getId(), pmPosition.getId(), newDescription, newIntroduction, newExperience, newSkillIds);
        
        assertThat(result).isPresent();
        assertThat(result.get().getPositionEntity().getName()).isEqualTo("프로덕트 매니저");
        assertThat(result.get().getDescription()).isEqualTo(newDescription);
        assertThat(result.get().getIntroduction()).isEqualTo(newIntroduction);
        assertThat(result.get().getExperience()).isEqualTo(newExperience);
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
    @DisplayName("특정 지역의 멘토 리스트를 조회합니다")
    void getMentorsByProvince_Success() {
        // given
        Province province = provinceRepository.findAll().stream()
                .filter(p -> p.getName().equals("서울특별시"))
                .findFirst().orElse(null);
        assertThat(province).isNotNull();

        // when
        List<MentorSummaryResponseDto> mentors = mentorService.getMentorsByProvince(province.getId());

        // then
        assertThat(mentors).isNotNull();
        assertThat(mentors).isNotEmpty();
        assertThat(mentors.get(0).getProvince().getName()).isEqualTo("서울특별시");
    }

}