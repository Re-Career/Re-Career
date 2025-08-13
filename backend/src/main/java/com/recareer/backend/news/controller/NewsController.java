package com.recareer.backend.news.controller;

import com.recareer.backend.news.dto.NewsDto;
import com.recareer.backend.news.service.NewsService;
import com.recareer.backend.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
@Tag(name = "News", description = "뉴스 API")
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    @Operation(summary = "뉴스 리스트 조회", description = "등록된 모든 뉴스를 조회합니다.")
    public ResponseEntity<ApiResponse<List<NewsDto>>> getAllNews() {
        List<NewsDto> newsList = newsService.getAllNews();
        return ResponseEntity.ok(ApiResponse.success(newsList));
    }
}