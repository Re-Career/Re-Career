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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Commit;

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
    
    private User mentorUser;
    private Mentor mentor;

    @BeforeEach
    void setUp() {
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
                .region("서울시 강남구")
                .build();
        
        userRepository.save(mentorUser);

        // 멘토에 성향 태그 추가
        UserPersonalityTag mentorTag1 = UserPersonalityTag.builder()
                .user(mentorUser)
                .personalityTag(tag1)
                .build();
        userPersonalityTagRepository.save(mentorTag1);
        
        mentor = Mentor.builder()
                .id(mentorUser.getId())
                .user(mentorUser)
                .position("시니어 백엔드 개발자")
                .description("5년차 백엔드 개발자입니다. Spring Boot 전문가입니다.")
                .isVerified(true)
                .build();
        
        mentorRepository.save(mentor);
    }

    @Test
    @DisplayName("검증된 멘토 ID로 멘토 조회 성공")
    void getVerifiedMentorById_Success() {
        Optional<Mentor> result = mentorService.getVerifiedMentorById(mentor.getId());
        
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(mentor.getId());
        assertThat(result.get().getUser().getName()).isEqualTo("멘토 김민주");
        assertThat(result.get().getIsVerified()).isTrue();
    }

    @Test
    @DisplayName("검증되지 않은 멘토는 조회되지 않음")
    void getVerifiedMentorById_NotVerified() {
        User unverifiedMentorUser = User.builder()
                .name("미인증 멘토")
                .email("unverified@test.com")
                .role(Role.MENTOR)
                .provider("google")
                .providerId("unverified123")
                .profileImageUrl("https://example.com/unverified.jpg")
                .region("서울시 서초구")
                .build();
        userRepository.save(unverifiedMentorUser);
        
        Mentor unverifiedMentor = Mentor.builder()
                .id(unverifiedMentorUser.getId())
                .user(unverifiedMentorUser)
                .position("주니어 개발자")
                .description("신입 개발자입니다.")
                .isVerified(false)
                .build();
        mentorRepository.save(unverifiedMentor);
        
        Optional<Mentor> result = mentorService.getVerifiedMentorById(unverifiedMentor.getId());
        
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("지역으로 멘토 검색 성공")
    void getMentorsByRegion_Success() {
        List<Mentor> result = mentorService.getMentorsByRegionAndPersonalityTags("강남", null);
        
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getUser().getRegion()).contains("강남");
    }

    @Test
    @DisplayName("지역이 null이면 기본 지역(서울시)으로 검색")
    void getMentorsByRegion_NullRegion() {
        mentorUser.updateProfile("멘토 김철수", "mentor@test.com", "https://example.com/mentor.jpg", "서울시");
        userRepository.save(mentorUser);
        
        List<Mentor> result = mentorService.getMentorsByRegionAndPersonalityTags(null, null);
        
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("멘토 정보 업데이트 성공")
    void updateMentor_Success() {
        String newPosition = "시니어 풀스택 개발자";
        String newDescription = "10년차 풀스택 개발자입니다.";
        
        Optional<Mentor> result = mentorService.updateMentor(mentor.getId(), newPosition, newDescription);
        
        assertThat(result).isPresent();
        assertThat(result.get().getPosition()).isEqualTo(newPosition);
        assertThat(result.get().getDescription()).isEqualTo(newDescription);
    }

    @Test
    @DisplayName("존재하지 않는 멘토 업데이트 실패")
    void updateMentor_NotFound() {
        Optional<Mentor> result = mentorService.updateMentor(999L, "새 포지션", "새 설명");
        
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
    @DisplayName("personalityTags 기반 멘토 필터링 테스트")
    void getMentorsByRegionAndPersonalityTags() {
        // setUp에서 생성된 tag1("적극적") ID 가져오기
        List<PersonalityTag> tags = personalityTagRepository.findAll();
        Long tag1Id = tags.stream()
                .filter(tag -> "적극적".equals(tag.getName()))
                .map(PersonalityTag::getId)
                .findFirst()
                .orElseThrow();
        Long tag2Id = tags.stream()
                .filter(tag -> "분석적".equals(tag.getName()))
                .map(PersonalityTag::getId)
                .findFirst()
                .orElseThrow();

        // personalityTags로 필터링 (적극적 태그)
        List<Long> personalityTags = List.of(tag1Id);
        List<Mentor> result = mentorService.getMentorsByRegionAndPersonalityTags("강남", personalityTags);

        // 결과 검증: 매칭되는 멘토가 우선 정렬되어야 함
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getUser().getName()).isEqualTo("멘토 김민주");
    }

    @Test
    @DisplayName("빈 personalityTags로 멘토 조회 시 전체 멘토 반환")
    void getMentorsByRegion_WithEmptyPersonalityTags() {
        List<Mentor> result = mentorService.getMentorsByRegionAndPersonalityTags("강남", List.of());
        
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getUser().getRegion()).contains("강남");
    }
}