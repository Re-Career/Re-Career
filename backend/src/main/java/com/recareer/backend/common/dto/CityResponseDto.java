package com.recareer.backend.common.dto;

import com.recareer.backend.common.entity.City;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityResponseDto {
    
    private Long id;
    private String key;
    private String name;
    private Long provinceId;
    
    public static CityResponseDto from(City city) {
        return CityResponseDto.builder()
                .id(city.getId())
                .key(city.getKey())
                .name(city.getName())
                .provinceId(city.getProvince().getId())
                .build();
    }
}