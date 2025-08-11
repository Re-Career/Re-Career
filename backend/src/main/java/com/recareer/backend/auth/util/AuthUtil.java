package com.recareer.backend.auth.util;

import com.recareer.backend.auth.service.JwtTokenProvider;
import com.recareer.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtil {
    
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    
    /**
     * Authorization 헤더에서 JWT 토큰을 추출하고 검증하여 사용자 ID를 반환
     * 
     * @param accessToken Authorization 헤더 값 ("Bearer {token}" 형태)
     * @return 검증된 사용자 ID
     * @throws IllegalArgumentException 토큰이 유효하지 않을 경우
     */
    public Long validateTokenAndGetUserId(String accessToken) {
        String token = accessToken.replace("Bearer ", "");
        
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 액세스 토큰입니다");
        }

        String providerId = jwtTokenProvider.getProviderIdFromToken(token);
        return userService.findByProviderId(providerId).getId();
    }
}