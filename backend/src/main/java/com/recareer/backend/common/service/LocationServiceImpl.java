package com.recareer.backend.common.service;

import com.recareer.backend.common.dto.CityResponseDto;
import com.recareer.backend.common.dto.ProvinceResponseDto;
import com.recareer.backend.common.repository.CityRepository;
import com.recareer.backend.common.repository.ProvinceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    
    private final ProvinceRepository provinceRepository;
    private final CityRepository cityRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<ProvinceResponseDto> getAllProvinces() {
        log.info("Getting all provinces");
        return provinceRepository.findAll().stream()
                .map(ProvinceResponseDto::from)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CityResponseDto> getCitiesByProvinceId(Long provinceId) {
        log.info("Getting cities for province id: {}", provinceId);
        return cityRepository.findByProvinceId(provinceId).stream()
                .map(CityResponseDto::from)
                .toList();
    }
}