package com.recareer.backend.mentor.dto;

import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.user.entity.UserPersonalityTag;
import com.recareer.backend.career.entity.MentorCareer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorSummaryResponseDto {

    private Long id;
    private String name;
    private PositionSummaryDto position;
    private String profileImageUrl;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PositionSummaryDto {
        private Long id;
        private String name;
    }

    public static MentorSummaryResponseDto from(Mentor mentor, List<UserPersonalityTag> userPersonalityTags, List<MentorCareer> careers) {
        return MentorSummaryResponseDto.builder()
                .id(mentor.getId())
                .name(mentor.getUser().getName())
                .position(mentor.getPositionEntity() != null ? PositionSummaryDto.builder()
                        .id(mentor.getPositionEntity().getId())
                        .name(mentor.getPositionEntity().getName())
                        .build() : null)
                .profileImageUrl(mentor.getUser().getProfileImageUrl())
                .build();
    }

}