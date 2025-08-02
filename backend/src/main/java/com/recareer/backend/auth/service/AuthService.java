package com.recareer.backend.auth.service;

import com.recareer.backend.auth.dto.SignupRequestDto;
import com.recareer.backend.user.dto.UserInfoDto;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.repository.MentorRepository;
import com.recareer.backend.user.entity.Role;
import com.recareer.backend.user.entity.User;
import com.recareer.backend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final MentorRepository mentorRepository;

    @Transactional(readOnly = true)
    public UserInfoDto getUserInfo(String providerId) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        
        return UserInfoDto.from(user);
    }

    @Transactional
    public UserInfoDto signup(String providerId, SignupRequestDto signupRequest) {
        
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 이메일 중복 검사
        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // MENTOR일 때 필수 필드 검증
        if (signupRequest.getRole() == Role.MENTOR) {
            if (signupRequest.getPosition() == null || signupRequest.getPosition().trim().isEmpty()) {
                throw new IllegalArgumentException("멘토는 직무/포지션이 필수입니다.");
            }
            if (signupRequest.getDescription() == null || signupRequest.getDescription().trim().isEmpty()) {
                throw new IllegalArgumentException("멘토는 자기소개가 필수입니다.");
            }
            if (signupRequest.getProfileImageUrl() == null || signupRequest.getProfileImageUrl().trim().isEmpty()) {
                throw new IllegalArgumentException("멘토는 프로필 이미지가 필수입니다.");
            }
        }

        // 사용자 정보 업데이트
        User updatedUser = User.builder()
                .id(user.getId())
                .name(signupRequest.getName())
                .email(signupRequest.getEmail())
                .role(signupRequest.getRole())
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .profileImageUrl(signupRequest.getProfileImageUrl() != null ? signupRequest.getProfileImageUrl() : user.getProfileImageUrl())
                .build();

        User savedUser = userRepository.save(updatedUser);

        // MENTOR일 때 mentors 테이블에 추가 정보 저장
        if (signupRequest.getRole() == Role.MENTOR) {
            Mentor mentor = Mentor.builder()
                    .user(savedUser)
                    .position(signupRequest.getPosition())
                    .description(signupRequest.getDescription())
                    .build();
            
            mentorRepository.save(mentor);
        }

        log.info("회원가입 완료 - email: {}, role: {}", signupRequest.getEmail(), signupRequest.getRole());
        return UserInfoDto.from(savedUser);
    }
}