package com.recareer.backend.position.controller;

import com.recareer.backend.position.dto.PositionDto;
import com.recareer.backend.position.dto.PositionSimpleDto;
import com.recareer.backend.position.dto.RegionPositionResponseDto;
import com.recareer.backend.position.service.PositionService;
import com.recareer.backend.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/positions")
@RequiredArgsConstructor
@Tag(name = "Position", description = "직무 정보 관리 API")
public class PositionController {

    private final PositionService positionService;

    @GetMapping("/by-location")
    @Operation(summary = "지역별 직무 조회", description = "특정 지역(시/도 또는 구/군)의 멘토가 많은 직무 4개 또는 랜덤 직무 4개를 조회합니다.")
    public ResponseEntity<ApiResponse<RegionPositionResponseDto>> getPositionsByLocation(@RequestParam String location) {
        RegionPositionResponseDto response = positionService.getPositionsByLocation(location);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/trend-20")
    @Operation(summary = "트렌드 TOP 20 직무 조회", description = "최근 트렌드 20 직무 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<List<PositionSimpleDto>>> getTrand20Positions() {
        List<PositionSimpleDto> trand20Positions = positionService.getTrand20Positions();
        return ResponseEntity.ok(ApiResponse.success(trand20Positions));
    }

    @GetMapping("/{id}")
    @Operation(summary = "직무 상세 조회", description = "특정 직무의 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<PositionDto>> getPositionById(@PathVariable Long id) {
        PositionDto position = positionService.getPositionById(id);
        return ResponseEntity.ok(ApiResponse.success(position));
    }

}