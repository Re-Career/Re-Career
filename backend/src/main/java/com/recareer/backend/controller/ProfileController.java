package com.recareer.backend.controller;

import com.recareer.backend.auth.service.JwtTokenProvider;
import com.recareer.backend.response.ApiResponse;
import com.recareer.backend.service.ProfileService;
import com.recareer.backend.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "프로필 관련 API")
public class ProfileController {

    private final S3Service s3Service;
    private final ProfileService profileService;
    private final JwtTokenProvider jwtTokenProvider;
    
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @PostMapping("/upload-image")
    @Operation(summary = "프로필 이미지 업로드", description = "사용자의 프로필 이미지를 업로드합니다.")
    public ResponseEntity<ApiResponse<String>> uploadProfileImageUrl(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam("file") MultipartFile file) {
        
        try {
            String token = accessToken.replace("Bearer ", "");
            
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid access token"));
            }

            validateFile(file);
            
            String providerId = jwtTokenProvider.getEmailFromToken(token);
            String imageUrl = s3Service.uploadFile(file, "profile-images");
            
            profileService.updateProfileImageUrl(providerId, imageUrl);
            
            return ResponseEntity.ok(ApiResponse.success(imageUrl, "프로필 이미지가 성공적으로 업로드되었습니다."));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (IOException e) {
            log.error("File upload failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponse.error("파일 업로드에 실패했습니다."));
        } catch (Exception e) {
            log.error("Profile image update failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponse.error("프로필 이미지 업데이트에 실패했습니다."));
        }
    }

    @DeleteMapping("/delete-image")
    @Operation(summary = "프로필 이미지 삭제", description = "사용자의 프로필 이미지를 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> deleteProfileImageUrl(
            @RequestHeader("Authorization") String accessToken) {
        
        try {
            String token = accessToken.replace("Bearer ", "");
            
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid access token"));
            }

            String providerId = jwtTokenProvider.getEmailFromToken(token);
            profileService.deleteProfileImageUrl(providerId);
            
            return ResponseEntity.ok(ApiResponse.success(null, "프로필 이미지가 성공적으로 삭제되었습니다."));
            
        } catch (Exception e) {
            log.error("Profile image deletion failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponse.error("프로필 이미지 삭제에 실패했습니다."));
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기는 5MB를 초과할 수 없습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("유효하지 않은 파일 형식입니다.");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. (jpg, jpeg, png, gif만 허용)");
        }
    }
}