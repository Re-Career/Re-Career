package com.recareer.backend.common.controller;

import com.recareer.backend.common.dto.CityResponseDto;
import com.recareer.backend.common.dto.ProvinceResponseDto;
import com.recareer.backend.common.service.LocationService;
import com.recareer.backend.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
@Tag(name = "Location", description = "지역 관련 API")
public class LocationController {
    
    private final LocationService locationService;
    
    @GetMapping("/provinces")
    @Operation(summary = "시/도 목록 조회", description = "모든 시/도 목록을 조회합니다")
    public ResponseEntity<ApiResponse<List<ProvinceResponseDto>>> getAllProvinces() {
        try {
            List<ProvinceResponseDto> provinces = locationService.getAllProvinces();
            return ResponseEntity.ok(ApiResponse.success(provinces));
        } catch (Exception e) {
            log.error("Get all provinces failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponse.error("시/도 목록 조회에 실패했습니다."));
        }
    }
    
    @GetMapping("/provinces/{provinceId}/cities")
    @Operation(summary = "시/군/구 목록 조회", description = "특정 시/도에 속한 시/군/구 목록을 조회합니다")
    public ResponseEntity<ApiResponse<List<CityResponseDto>>> getCitiesByProvinceId(@PathVariable Long provinceId) {
        try {
            List<CityResponseDto> cities = locationService.getCitiesByProvinceId(provinceId);
            return ResponseEntity.ok(ApiResponse.success(cities));
        } catch (Exception e) {
            log.error("Get cities by province id failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponse.error("시/군/구 목록 조회에 실패했습니다."));
        }
    }
}