package com.recareer.backend.user.controller;

import com.recareer.backend.user.dto.UpdateUserPersonalityTagsDto;
import com.recareer.backend.user.dto.UserInfoDto;
import com.recareer.backend.user.dto.UpdateUserProfileDto;
import com.recareer.backend.auth.service.JwtTokenProvider;
import com.recareer.backend.response.ApiResponse;
import com.recareer.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 관련 API")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @GetMapping("/profile")
    @Operation(summary = "사용자 프로필 조회", description = "현재 로그인한 사용자의 프로필 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<UserInfoDto>> getUserProfile(
            @RequestHeader("Authorization") String accessToken) {
        
        try {
            String token = accessToken.replace("Bearer ", "");
            
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid access token"));
            }

            String providerId = jwtTokenProvider.getProviderIdFromToken(token);
            UserInfoDto userInfo = userService.getUserProfile(providerId);
            
            return ResponseEntity.ok(ApiResponse.success(userInfo));
            
        } catch (Exception e) {
            log.error("Get user profile failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponse.error("프로필 조회에 실패했습니다."));
        }
    }

    @PutMapping("/profile")
    @Operation(summary = "사용자 프로필 업데이트", description = "사용자의 기본 정보와 멘토인 경우 멘토 정보를 업데이트합니다.")
    public ResponseEntity<ApiResponse<UserInfoDto>> updateUserProfile(
            @RequestHeader("Authorization") String accessToken,
            @Valid @RequestBody UpdateUserProfileDto request) {
        
        try {
            String token = accessToken.replace("Bearer ", "");
            
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid access token"));
            }

            String providerId = jwtTokenProvider.getProviderIdFromToken(token);
            UserInfoDto userInfo = userService.updateUserProfile(providerId, request);
            
            return ResponseEntity.ok(ApiResponse.success("프로필이 성공적으로 업데이트되었습니다.", userInfo));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Profile update failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponse.error("프로필 업데이트에 실패했습니다."));
        }
    }

    @PutMapping("/profile/image")
    @Operation(summary = "프로필 이미지 업로드", description = "사용자의 프로필 이미지를 업로드합니다.")
    public ResponseEntity<ApiResponse<String>> updateProfileImage(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam("file") MultipartFile file) {
        
        try {
            String token = accessToken.replace("Bearer ", "");
            
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid access token"));
            }

            validateFile(file);
            
            String providerId = jwtTokenProvider.getProviderIdFromToken(token);
            String imageUrl = userService.updateProfileImage(providerId, file);
            
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

    @DeleteMapping("/profile/image")
    @Operation(summary = "프로필 이미지 삭제", description = "사용자의 프로필 이미지를 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> deleteProfileImage(
            @RequestHeader("Authorization") String accessToken) {
        
        try {
            String token = accessToken.replace("Bearer ", "");
            
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid access token"));
            }

            String providerId = jwtTokenProvider.getProviderIdFromToken(token);
            userService.deleteProfileImage(providerId);
            
            return ResponseEntity.ok(ApiResponse.success(null, "프로필 이미지가 성공적으로 삭제되었습니다."));
            
        } catch (Exception e) {
            log.error("Profile image deletion failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponse.error("프로필 이미지 삭제에 실패했습니다."));
        }
    }

    @PutMapping("/personality-tags")
    @Operation(summary = "사용자 성향 태그 수정", description = "사용자의 성향 태그를 선택/수정합니다. (최대 5개)")
    public ResponseEntity<ApiResponse<String>> updateUserPersonalityTags(
            @RequestHeader("Authorization") String accessToken,
            @Valid @RequestBody UpdateUserPersonalityTagsDto request) {
        
        try {
            String token = accessToken.replace("Bearer ", "");
            
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid access token"));
            }
            
            String providerId = jwtTokenProvider.getProviderIdFromToken(token);
            userService.updateUserPersonalityTags(providerId, request);
            
            return ResponseEntity.ok(ApiResponse.success("성향 태그가 성공적으로 업데이트되었습니다."));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("User personality tags update failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponse.error("성향 태그 업데이트에 실패했습니다."));
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