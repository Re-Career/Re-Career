package com.recareer.backend.service;

import com.recareer.backend.user.entity.User;
import com.recareer.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final UserRepository userRepository;
    private final S3Service s3Service;

    public void updateProfileImageUrl(String providerId, String imageUrl) {
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

        // User 엔티티의 update 메서드 사용
        user.update(user.getName(), imageUrl);
        userRepository.save(user);
        
        log.info("Profile image updated for user: {}", providerId);
    }

    public void deleteProfileImageUrl(String providerId) {
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
}