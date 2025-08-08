package com.recareer.backend.auth.dto;

import com.recareer.backend.user.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequestDto {

    @Schema(description = "사용자 이름", example = "김멘토")
    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @Schema(description = "이메일", example = "mentor@example.com")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @Schema(description = "역할", example = "MENTOR")
    @NotNull(message = "멘토와 멘티 중 하나를 선택해주세요.")
    private Role role;

    @Schema(description = "프로필 이미지 URL (MENTOR일 때 필수)", example = "https://example.com/profile.jpg")
    private String profileImageUrl;

    @Schema(description = "지역", example = "경기도 하남시")
    @NotBlank(message = "지역은 필수입니다.")
    private String region;

    @Schema(description = "선택한 성향 태그 ID 목록 (최대 5개)", example = "[1, 2, 3]")
    @Size(max = 5, message = "성향 태그는 최대 5개까지 선택할 수 있습니다.")
    private List<Long> personalityTagIds;

    // Mentor 전용 필드들
    @Schema(description = "직무/포지션 (MENTOR일 때 필수)", example = "백엔드 개발자")
    private String position;

    @Schema(description = "자기소개 (MENTOR일 때 필수)", example = "5년차 백엔드 개발자입니다. Spring Boot와 AWS 경험이 풍부합니다.")
    private String description;
}