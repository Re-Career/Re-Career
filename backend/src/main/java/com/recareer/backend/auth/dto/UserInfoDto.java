package com.recareer.backend.auth.dto;

import com.recareer.backend.user.entity.Role;
import com.recareer.backend.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDto {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 이름", example = "김멘토")
    private String name;

    @Schema(description = "이메일", example = "mentor@example.com")
    private String email;

    @Schema(description = "역할", example = "MENTOR")
    private Role role;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImage;

    @Schema(description = "가입 완료 여부", example = "true")
    private boolean isSignupCompleted;

    public static UserInfoDto from(User user) {
        return UserInfoDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .profileImage(user.getProfileImage())
                .isSignupCompleted(user.getName() != null && user.getEmail() != null && user.getRole() != null)
                .build();
    }
}