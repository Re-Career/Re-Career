package com.recareer.backend.personality.controller;

import com.recareer.backend.personality.dto.PersonalityTagDto;
import com.recareer.backend.personality.service.PersonalityTagService;
import com.recareer.backend.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/personality-tags")
@RequiredArgsConstructor
@Tag(name = "PersonalityTag", description = "성향 태그 관련 API")
public class PersonalityTagController {

    private final PersonalityTagService personalityTagService;

    @GetMapping
    @Operation(summary = "성향 태그 목록 조회")
    public ResponseEntity<ApiResponse<List<PersonalityTagDto>>> getAllPersonalityTags() {
        List<PersonalityTagDto> personalityTags = personalityTagService.getAllPersonalityTags();
        return ResponseEntity.ok(ApiResponse.success(personalityTags));
    }
}