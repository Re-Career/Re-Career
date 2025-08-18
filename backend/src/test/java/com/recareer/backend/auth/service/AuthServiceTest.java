package com.recareer.backend.auth.service;

import com.recareer.backend.auth.dto.SignupRequestDto;
import com.recareer.backend.user.entity.Role;
import com.recareer.backend.user.entity.User;
import com.recareer.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserRepository userRepository;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .provider("google")
                .providerId("test123")
                .build();
        userRepository.save(testUser);
    }
    
    @Test
    @DisplayName("멘토 회원가입 시 프로필 이미지가 없으면 예외 발생")
    void signup_MentorWithoutProfileImage_ThrowsException() {
        SignupRequestDto mentorRequest = SignupRequestDto.builder()
                .name("테스트 멘토")
                .email("mentor@test.com")
                .role(Role.MENTOR)
                .position("백엔드 개발자")
                .description("5년차 개발자입니다")
                .provinceId(1L)
                .personalityTagIds(List.of())
                // profileImageUrl이 null
                .build();
        
        assertThatThrownBy(() -> authService.signup("test123", mentorRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("멘토는 프로필 이미지가 필수입니다.");
    }
    
    @Test
    @DisplayName("멘토 회원가입 시 프로필 이미지가 빈 문자열이면 예외 발생")
    void signup_MentorWithEmptyProfileImage_ThrowsException() {
        SignupRequestDto mentorRequest = SignupRequestDto.builder()
                .name("테스트 멘토")
                .email("mentor@test.com")
                .role(Role.MENTOR)
                .position("백엔드 개발자")
                .description("5년차 개발자입니다")
                .provinceId(1L)
                .personalityTagIds(List.of())
                .profileImageUrl("") // 빈 문자열
                .build();
        
        assertThatThrownBy(() -> authService.signup("test123", mentorRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("멘토는 프로필 이미지가 필수입니다.");
    }
    
    @Test
    @DisplayName("멘티 회원가입 시 프로필 이미지가 없어도 성공")
    void signup_MenteeWithoutProfileImage_Success() {
        SignupRequestDto menteeRequest = SignupRequestDto.builder()
                .name("테스트 멘티")
                .email("mentee@test.com")
                .role(Role.MENTEE)
                .provinceId(1L)
                .personalityTagIds(List.of())
                // profileImageUrl이 null이어도 OK
                .build();
        
        // 예외가 발생하지 않아야 함
        authService.signup("test123", menteeRequest);
        
        // 저장된 유저 확인
        Optional<User> savedUser = userRepository.findByProviderId("test123");
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getRole()).isEqualTo(Role.MENTEE);
        assertThat(savedUser.get().getName()).isEqualTo("테스트 멘티");
    }
}