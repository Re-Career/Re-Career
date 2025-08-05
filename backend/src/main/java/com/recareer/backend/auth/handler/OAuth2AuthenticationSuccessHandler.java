package com.recareer.backend.auth.handler;

import com.recareer.backend.auth.service.JwtTokenProvider;
import com.recareer.backend.user.entity.User;
import com.recareer.backend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Value("${app.oauth2.authorized-redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            log.debug("이미 응답이 클라이언트에 전송된 상태로 리다이렉트할 수 없습니다." + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Object idAttribute = oAuth2User.getAttribute("id");
        String kakaoId;
        
        if (idAttribute instanceof Long) {
            kakaoId = String.valueOf(idAttribute);
        } else if (idAttribute instanceof String) {
            kakaoId = (String) idAttribute;
        } else {
            kakaoId = idAttribute.toString();
        }
        
        // JWT 토큰 생성 (providerId를 subject로 사용, 기본 권한은 MENTEE)
        String roleKey = "ROLE_MENTEE"; // 기본값으로 설정
        
        // 사용자가 DB에 존재하는지 확인하고 역할 업데이트
        userRepository.findByProviderId(kakaoId).ifPresent(user -> {
            // 사용자가 존재하면 실제 역할로 업데이트 (하지만 이미 기본값으로 처리)
        });
        
        String accessToken = jwtTokenProvider.createAccessToken(kakaoId, roleKey);
        String refreshToken = jwtTokenProvider.createRefreshToken(kakaoId);

        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();
    }
}