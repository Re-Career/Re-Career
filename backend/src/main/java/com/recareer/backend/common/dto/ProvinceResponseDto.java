package com.recareer.backend.common.dto;

import com.recareer.backend.common.entity.Province;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvinceResponseDto {
    
    private Long id;
    private String key;
    private String name;
    
    public static ProvinceResponseDto from(Province province) {
        return ProvinceResponseDto.builder()
                .id(province.getId())
                .key(province.getKey())
                .name(province.getName())
                .build();
    }
}