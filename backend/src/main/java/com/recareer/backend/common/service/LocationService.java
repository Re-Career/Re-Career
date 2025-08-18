package com.recareer.backend.common.service;

import com.recareer.backend.common.dto.CityResponseDto;
import com.recareer.backend.common.dto.ProvinceResponseDto;

import java.util.List;

public interface LocationService {
    
    List<ProvinceResponseDto> getAllProvinces();
    
    List<CityResponseDto> getAllCities();
    
    List<CityResponseDto> getCitiesByProvinceId(Long provinceId);
}