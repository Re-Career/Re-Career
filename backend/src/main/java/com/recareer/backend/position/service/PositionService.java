package com.recareer.backend.position.service;

import com.recareer.backend.mentor.repository.MentorRepository;
import com.recareer.backend.position.dto.PositionDto;
import com.recareer.backend.position.dto.PositionResponsibilitiesDto;
import com.recareer.backend.position.dto.PositionSimpleDto;
import com.recareer.backend.position.dto.RegionPositionResponseDto;
import com.recareer.backend.position.entity.Position;
import com.recareer.backend.position.entity.PositionResponsibilities;
import com.recareer.backend.position.entity.PositionResponsibilityMap;
import com.recareer.backend.position.repository.PositionRepository;
import com.recareer.backend.position.repository.PositionResponsibilityMapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionRepository positionRepository;
    private final PositionResponsibilityMapRepository positionResponsibilityMapRepository;
    private final MentorRepository mentorRepository;

    @Transactional(readOnly = true)
    public List<PositionSimpleDto> getTrand20Positions() {
        return positionRepository.findByTrendRankIsNotNullAndTrendRankLessThanEqualOrderByTrendRankAsc(20).stream()
                .map(this::convertToSimpleDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PositionDto getPositionById(Long id) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Position not found with id: " + id));
        
        List<PositionResponsibilityMap> maps = positionResponsibilityMapRepository.findByPositionId(id);
        List<PositionResponsibilitiesDto> responsibilities = maps.stream()
                .map(map -> convertToDto(map.getPositionResponsibilities()))
                .collect(Collectors.toList());
        
        return convertToDtoWithResponsibilities(position, responsibilities);
    }

    @Transactional(readOnly = true)
    public RegionPositionResponseDto getPositionsByRegion(String region) {
        List<PositionSimpleDto> positions;
        String resultRegion;
        
        if ("all".equalsIgnoreCase(region)) {
            // region이 "all"이면 trendRank가 null인 것들을 랜덤으로 4개 조회
            List<Position> randomPositions = positionRepository.findRandomPositionsWithNullTrendRank(4);
            positions = randomPositions.stream()
                    .map(this::convertToSimpleDto)
                    .collect(Collectors.toList());
            resultRegion = "all";
        } else {
            // 특정 지역의 멘토들의 position count 조회 (상위 4개)
            List<Object[]> positionCounts = mentorRepository.countPositionsByRegion(region);
            
            if (positionCounts.size() >= 4) {
                // 4개 이상이면 상위 4개의 position name으로 Position 조회
                List<String> topPositionNames = positionCounts.stream()
                        .limit(4)
                        .map(result -> (String) result[0])
                        .collect(Collectors.toList());
                
                List<Position> topPositions = positionRepository.findByNameIn(topPositionNames);
                positions = topPositions.stream()
                        .map(this::convertToSimpleDto)
                        .collect(Collectors.toList());
                resultRegion = region;
            } else {
                // 4개 미만이면 trendRank가 null인 것들을 랜덤으로 4개 조회
                List<Position> randomPositions = positionRepository.findRandomPositionsWithNullTrendRank(4);
                positions = randomPositions.stream()
                        .map(this::convertToSimpleDto)
                        .collect(Collectors.toList());
                resultRegion = "all";
            }
        }
        
        return RegionPositionResponseDto.builder()
                .region(resultRegion)
                .positions(positions)
                .build();
    }

    private PositionDto convertToDtoWithResponsibilities(Position position, List<PositionResponsibilitiesDto> responsibilities) {
        return PositionDto.builder()
                .id(position.getId())
                .name(position.getName())
                .trendRank(position.getTrendRank())
                .imageUrl(position.getImageUrl())
                .category(position.getCategory())
                .description(position.getDescription())
                .industryTrends(position.getIndustryTrends())
                .positionResponsibilities(responsibilities)
                .build();
    }

    private PositionSimpleDto convertToSimpleDto(Position position) {
        return PositionSimpleDto.builder()
                .id(position.getId())
                .name(position.getName())
                .imageUrl(position.getImageUrl())
                .category(position.getCategory())
                .build();
    }

    private PositionResponsibilitiesDto convertToDto(PositionResponsibilities responsibility) {
        return PositionResponsibilitiesDto.builder()
                .id(responsibility.getId())
                .name(responsibility.getName())
                .imageUrl(responsibility.getImageUrl())
                .build();
    }
}