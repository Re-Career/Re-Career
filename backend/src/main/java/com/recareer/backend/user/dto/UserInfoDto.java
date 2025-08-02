package com.recareer.backend.user.dto;

import com.recareer.backend.mentor.entity.Mentor;
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
    private String profileImageUrl;

    @Schema(description = "가입 완료 여부", example = "true")
    private boolean isSignupCompleted;

    // 멘토 정보 (역할이 MENTOR일 때만)
    @Schema(description = "멘토 ID", example = "1")
    private Long mentorId;

    @Schema(description = "멘토 포지션", example = "백엔드 개발자")
    private String mentorPosition;

    @Schema(description = "멘토 이력", example = "5년차 백엔드 개발자입니다.")
    private String mentorDescription;

    public static UserInfoDto from(User user) {
        return UserInfoDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .profileImageUrl(user.getProfileImageUrl())
                .isSignupCompleted(user.getName() != null && user.getEmail() != null && user.getRole() != null)
                .build();
    }

    public static UserInfoDto from(User user, Mentor mentor) {
        UserInfoDtoBuilder builder = UserInfoDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .profileImageUrl(user.getProfileImageUrl())
                .isSignupCompleted(user.getName() != null && user.getEmail() != null && user.getRole() != null);

        if (mentor != null) {
            builder.mentorId(mentor.getId())
                   .mentorPosition(mentor.getPosition())
                   .mentorDescription(mentor.getDescription());
        }

        return builder.build();
    }
}