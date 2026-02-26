package com.payper.server.map.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class NearbySearchResponse {

    @Schema(description = "반경 내 Top10 가맹점 지점 정보")
    public record NearbyPlaceItem(
            @Schema(description = "Top10 가맹점 우선순위 (1 = 최우선)", example = "1")
            int merchantRank,

            @Schema(description = "가맹점명", example = "스타벅스") String merchantName,

            @Schema(description = "지점명", example = "스타벅스 강남점")
            String placeName,

            @Schema(description = "지점 위도", example = "37.4979")
            double latitude,

            @Schema(description = "지점 경도", example = "127.0276")
            double longitude,

            @Schema(description = "사용자로부터의 거리 (km)", example = "0.35")
            double distanceKm) {}
}
