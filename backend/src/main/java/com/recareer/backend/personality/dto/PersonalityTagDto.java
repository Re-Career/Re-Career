package com.recareer.backend.personality.dto;

import com.recareer.backend.personality.entity.PersonalityTag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalityTagDto {

    @Schema(description = "성향 태그 ID")
    private Long id;

    @Schema(description = "성향 태그 이름", example = "논리적")
    private String name;

    public static PersonalityTagDto from(PersonalityTag personalityTag) {
        return PersonalityTagDto.builder()
                .id(personalityTag.getId())
                .name(personalityTag.getName())
                .build();
    }
}