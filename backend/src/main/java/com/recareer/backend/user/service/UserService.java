package com.recareer.backend.user.service;

import com.recareer.backend.personality.dto.PersonalityTagDto;
import com.recareer.backend.personality.repository.PersonalityTagRepository;
import com.recareer.backend.user.dto.UpdateUserPersonalityTagsDto;
import com.recareer.backend.user.dto.UserInfoDto;
import com.recareer.backend.user.dto.UpdateUserProfileDto;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.repository.MentorRepository;
import com.recareer.backend.user.entity.Role;
import com.recareer.backend.user.entity.User;
import com.recareer.backend.user.entity.UserPersonalityTag;
import com.recareer.backend.user.repository.UserPersonalityTagRepository;
import com.recareer.backend.user.repository.UserRepository;
import com.recareer.backend.common.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final MentorRepository mentorRepository;
    private final UserPersonalityTagRepository userPersonalityTagRepository;
    private final PersonalityTagRepository personalityTagRepository;
    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public UserInfoDto getUserProfile(String providerId) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 사용자의 성향 태그 조회
        List<PersonalityTagDto> personalityTags = userPersonalityTagRepository.findByUserId(user.getId())
                .stream()
                .map(userPersonalityTag -> PersonalityTagDto.from(userPersonalityTag.getPersonalityTag()))
                .toList();
        
        // 사용자가 MENTOR인 경우 멘토 정보도 함께 조회
        if (user.getRole() == Role.MENTOR) {
            Mentor mentor = mentorRepository.findByUser(user).orElse(null);
            UserInfoDto userInfo = UserInfoDto.from(user, mentor);
            return UserInfoDto.builder()
                    .id(userInfo.getId())
                    .name(userInfo.getName())
                    .email(userInfo.getEmail())
                    .role(userInfo.getRole())
                    .region(userInfo.getRegion())
                    .profileImageUrl(userInfo.getProfileImageUrl())
                    .isSignupCompleted(userInfo.isSignupCompleted())
                    .mentorId(userInfo.getMentorId())
                    .mentorPosition(userInfo.getMentorPosition())
                    .mentorDescription(userInfo.getMentorDescription())
                    .mentorIsVerified(userInfo.getMentorIsVerified())
                    .personalityTags(personalityTags)
                    .build();
        }
        
        UserInfoDto userInfo = UserInfoDto.from(user);
        return UserInfoDto.builder()
                .id(userInfo.getId())
                .name(userInfo.getName())
                .email(userInfo.getEmail())
                .role(userInfo.getRole())
                .region(userInfo.getRegion())
                .profileImageUrl(userInfo.getProfileImageUrl())
                .isSignupCompleted(userInfo.isSignupCompleted())
                .personalityTags(personalityTags)
                .build();
    }

    public String updateProfileImage(String providerId, MultipartFile file) throws IOException {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 기존 프로필 이미지가 있다면 S3에서 삭제
        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            try {
                s3Service.deleteFile(user.getProfileImageUrl());
            } catch (Exception e) {
                log.warn("Failed to delete old profile image: {}", e.getMessage());
            }
        }

        // 새 이미지 업로드
        String imageUrl = s3Service.uploadFile(file, "profile-images");
        
        // User 엔티티 업데이트
        user.update(user.getName(), imageUrl);
        userRepository.save(user);
        
        log.info("Profile image updated for user: {}", providerId);
        return imageUrl;
    }

    public void deleteProfileImage(String providerId) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            try {
                s3Service.deleteFile(user.getProfileImageUrl());
            } catch (Exception e) {
                log.warn("Failed to delete profile image from S3: {}", e.getMessage());
            }
        }

        // 프로필 이미지를 null로 설정
        user.update(user.getName(), null);
        userRepository.save(user);
        
        log.info("Profile image deleted for user: {}", providerId);
    }

    public UserInfoDto updateUserProfile(String providerId, UpdateUserProfileDto request) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 이메일 중복 검사 (현재 사용자 제외)
        if (!request.getEmail().equals(user.getEmail()) && 
            userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 사용자 기본 정보 업데이트
        user.updateProfile(request.getName(), request.getEmail(), user.getProfileImageUrl());
        userRepository.save(user);

        // 멘토인 경우 멘토 정보도 업데이트
        if (user.getRole() == Role.MENTOR) {
            if (request.getPosition() == null || request.getPosition().trim().isEmpty()) {
                throw new IllegalArgumentException("멘토는 직무/포지션이 필수입니다.");
            }
            if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
                throw new IllegalArgumentException("멘토는 자기소개가 필수입니다.");
            }

            Mentor mentor = mentorRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("멘토 정보를 찾을 수 없습니다."));
            
            mentor.update(request.getPosition(), request.getDescription());
            mentorRepository.save(mentor);
            
            log.info("User and mentor profile updated for user: {}", providerId);
            return UserInfoDto.from(user, mentor);
        }
        
        log.info("User profile updated for user: {}", providerId);
        return UserInfoDto.from(user);
    }

    public void updateUserPersonalityTags(String providerId, UpdateUserPersonalityTagsDto request) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 5개 초과 체크
        if (request.getPersonalityTagIds().size() > 5) {
            throw new IllegalArgumentException("성향 태그는 최대 5개까지 선택할 수 있습니다.");
        }

        // 존재하지 않는 태그 ID가 있는지 체크
        List<Long> invalidTagIds = request.getPersonalityTagIds().stream()
                .filter(tagId -> !personalityTagRepository.existsById(tagId))
                .toList();
        
        if (!invalidTagIds.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 성향 태그입니다: " + invalidTagIds);
        }

        // 기존 연결 제거
        userPersonalityTagRepository.deleteByUserId(user.getId());

        // 새로운 연결 생성
        List<UserPersonalityTag> userPersonalityTags = request.getPersonalityTagIds().stream()
                .map(tagId -> {
                    return UserPersonalityTag.builder()
                            .user(user)
                            .personalityTag(personalityTagRepository.findById(tagId).orElseThrow())
                            .build();
                })
                .toList();

        userPersonalityTagRepository.saveAll(userPersonalityTags);
        
        log.info("User personality tags updated for user: {} with {} tags", providerId, request.getPersonalityTagIds().size());
    }
}