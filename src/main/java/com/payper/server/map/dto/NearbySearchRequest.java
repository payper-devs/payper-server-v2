package com.payper.server.map.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Schema(description = "반경 내 Top10 가맹점 검색 요청")
public record NearbySearchRequest(
        @Schema(description = "사용자 위도", example = "37.5665") @NotNull
        Double latitude,

        @Schema(description = "사용자 경도", example = "126.9780") @NotNull
        Double longitude,

        @Schema(description = "검색 반경 (km, 0 초과)", example = "1.0")
        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        Double radiusKm) {}
