package com.recareer.backend.auth.dto;

import com.recareer.backend.user.entity.Role;
import com.recareer.backend.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String profileImage;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String profileImage) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("kakao".equals(registrationId)) {
            return ofKakao(userNameAttributeName, attributes);
        }
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        String kakaoId = String.valueOf(attributes.get("id"));
        
        return OAuthAttributes.builder()
                .name("카카오사용자_" + kakaoId)  // 임시 이름
                .email(null)  // 이메일은 별도 수집
                .profileImage(null)  // 프로필 이미지는 별도 수집
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .profileImage(profileImage)
                .role(Role.MENTEE)
                .provider("kakao")
                .providerId(String.valueOf(attributes.get("id")))
                .build();
    }
}