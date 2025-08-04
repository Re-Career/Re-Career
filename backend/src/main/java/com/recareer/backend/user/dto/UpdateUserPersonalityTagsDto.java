package com.recareer.backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class UpdateUserPersonalityTagsDto {

    @Schema(description = "선택한 성향 태그 ID 목록 (최대 5개)", example = "[1, 2, 3]")
    @NotNull(message = "성향 태그 목록은 필수입니다.")
    @Size(max = 5, message = "성향 태그는 최대 5개까지 선택할 수 있습니다.")
    private List<Long> personalityTagIds;
}