package com.recareer.backend.user.service;

import com.recareer.backend.user.dto.UserInfoDto;
import com.recareer.backend.user.dto.UpdateUserProfileDto;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.repository.MentorRepository;
import com.recareer.backend.user.entity.Role;
import com.recareer.backend.user.entity.User;
import com.recareer.backend.user.repository.UserRepository;
import com.recareer.backend.common.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final MentorRepository mentorRepository;
    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public UserInfoDto getUserProfile(String providerId) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 사용자가 MENTOR인 경우 멘토 정보도 함께 조회
        if (user.getRole() == Role.MENTOR) {
            Mentor mentor = mentorRepository.findByUser(user).orElse(null);
            return UserInfoDto.from(user, mentor);
        }
        
        return UserInfoDto.from(user);
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
}