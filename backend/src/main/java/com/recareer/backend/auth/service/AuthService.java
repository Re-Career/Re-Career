package com.recareer.backend.auth.service;

import com.recareer.backend.auth.dto.SignupRequestDto;
import com.recareer.backend.user.dto.UserInfoDto;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.repository.MentorRepository;
import com.recareer.backend.user.entity.Role;
import com.recareer.backend.user.entity.User;
import com.recareer.backend.user.entity.UserPersonalityTag;
import com.recareer.backend.user.repository.UserRepository;
import com.recareer.backend.user.repository.UserPersonalityTagRepository;
import com.recareer.backend.personality.entity.PersonalityTag;
import com.recareer.backend.personality.repository.PersonalityTagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final MentorRepository mentorRepository;
    private final UserPersonalityTagRepository userPersonalityTagRepository;
    private final PersonalityTagRepository personalityTagRepository;

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

        if (user.getName() != null && !user.getName().trim().isEmpty() && 
            user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("이미 회원가입이 완료된 사용자입니다. 프로필 수정을 이용해주세요.");
        }

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
                .region(signupRequest.getRegion())
                .build();

        User savedUser = userRepository.save(updatedUser);

        // MENTOR일 때 mentors 테이블에 추가 정보 저장
        if (signupRequest.getRole() == Role.MENTOR) {
            Mentor mentor = Mentor.builder()
                    .id(savedUser.getId())
                    .user(savedUser)
                    .description(signupRequest.getDescription())
                    .build();
            
            mentorRepository.save(mentor);
        }

        // 성향 태그 저장
        if (!CollectionUtils.isEmpty(signupRequest.getPersonalityTagIds())) {
            saveUserPersonalityTags(savedUser, signupRequest.getPersonalityTagIds());
        }

        log.info("회원가입 완료 - email: {}, role: {}", signupRequest.getEmail(), signupRequest.getRole());
        return UserInfoDto.from(savedUser);
    }

    private void saveUserPersonalityTags(User user, List<Long> personalityTagIds) {
        for (Long personalityTagId : personalityTagIds) {
            PersonalityTag personalityTag = personalityTagRepository.findById(personalityTagId)
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 성향 태그입니다: " + personalityTagId));
            
            UserPersonalityTag userPersonalityTag = UserPersonalityTag.builder()
                    .user(user)
                    .personalityTag(personalityTag)
                    .build();
            
            userPersonalityTagRepository.save(userPersonalityTag);
        }
    }
}