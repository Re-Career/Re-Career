package com.recareer.backend.position.service;

import com.recareer.backend.common.entity.City;
import com.recareer.backend.common.entity.Province;
import com.recareer.backend.common.repository.CityRepository;
import com.recareer.backend.common.repository.ProvinceRepository;
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
    private final ProvinceRepository provinceRepository;
    private final CityRepository cityRepository;

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
    public RegionPositionResponseDto getPositionsByProvince(Long provinceId, Long cityId) {
        List<PositionSimpleDto> positions;
        String resultProvince = "서울특별시";
        String resultCity = null;
        
        // 기본값 설정 (provinceId가 null이면 서울특별시 ID 1L 사용)
        Long finalProvinceId = provinceId != null ? provinceId : 1L;
        Long finalCityId = cityId;
        
        // Province 정보 조회
        Province province = provinceRepository.findById(finalProvinceId).orElse(null);
        if (province != null) {
            resultProvince = province.getName();
        }
        
        // City 정보 조회
        if (finalCityId != null) {
            City city = cityRepository.findById(finalCityId).orElse(null);
            if (city != null) {
                resultCity = city.getName();
                // City가 있으면 그 City의 Province로 업데이트
                resultProvince = city.getProvince().getName();
            }
        }
        
        // 지역 기반 멘토 position count 조회
        List<Object[]> positionCounts = mentorRepository.countPositionsByProvinceId(finalProvinceId);
        
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
        } else {
            // 4개 미만이면 trendRank가 null인 것들을 랜덤으로 4개 조회
            List<Position> randomPositions = positionRepository.findRandomPositionsWithNullTrendRank(4);
            positions = randomPositions.stream()
                    .map(this::convertToSimpleDto)
                    .collect(Collectors.toList());
        }
        
        return RegionPositionResponseDto.builder()
                .province(resultProvince)
                .city(resultCity)
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