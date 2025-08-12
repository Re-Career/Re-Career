package com.recareer.backend.news.service;

import com.recareer.backend.news.dto.NewsDto;
import com.recareer.backend.news.entity.News;
import com.recareer.backend.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    @Transactional(readOnly = true)
    public List<NewsDto> getAllNews() {
        return newsRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private NewsDto convertToDto(News news) {
        return NewsDto.builder()
                .id(news.getId())
                .title(news.getTitle())
                .description(news.getDescription())
                .url(news.getUrl())
                .category(news.getCategory())
                .imageUrl(news.getImageUrl())
                .build();
    }
}