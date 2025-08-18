package com.recareer.backend.auth.controller;

import com.recareer.backend.auth.dto.SignupRequestDto;
import com.recareer.backend.user.dto.UserInfoDto;
import com.recareer.backend.auth.service.AuthService;
import com.recareer.backend.auth.service.JwtTokenProvider;
import com.recareer.backend.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "인증 관련 API")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    @PostMapping("/refresh")
    @Operation(summary = "액세스 토큰 갱신")
    public ResponseEntity<ApiResponse<String>> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        String token = refreshToken.replace("Bearer ", "");
        
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid refresh token"));
        }

        String providerId = jwtTokenProvider.getProviderIdFromToken(token);
        String newAccessToken = jwtTokenProvider.createAccessToken(providerId, "ROLE_USER");

        return ResponseEntity.ok(ApiResponse.success(newAccessToken));
    }

    @GetMapping("/me")
    @Operation(summary = "현재 로그인한 사용자 정보 조회")
    public ResponseEntity<ApiResponse<UserInfoDto>> getCurrentUser(@RequestHeader("Authorization") String accessToken) {
        String token = accessToken.replace("Bearer ", "");
        
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid access token"));
        }

        String providerId = jwtTokenProvider.getProviderIdFromToken(token);
        UserInfoDto userInfo = authService.getUserInfo(providerId);

        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }

    @PostMapping(value = "/signup", consumes = "multipart/form-data")
    @Operation(summary = "회원가입 추가 정보 입력")
    public ResponseEntity<ApiResponse<UserInfoDto>> completeSignup(
        @RequestHeader("Authorization") String accessToken,
        @Valid @RequestBody SignupRequestDto signupRequest) {
        
        String token = accessToken.replace("Bearer ", "");
        
        if (!jwtTokenProvider.validateToken(token)) {
            log.warn("⚠️ 유효하지 않은 토큰으로 signup 시도");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid access token"));
        }

        String providerId = jwtTokenProvider.getProviderIdFromToken(token);
        UserInfoDto userInfo = authService.signup(providerId, signupRequest);
        
        log.info("🎉 Signup 성공 - userId: {}, role: {}", userInfo.getId(), userInfo.getRole());
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }
}