package com.payper.server.map.controller;

import com.payper.server.global.response.ApiResponse;
import com.payper.server.map.dto.NearbySearchRequest;
import com.payper.server.map.dto.NearbySearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "지도", description = "사용자 위치 기반 가맹점 검색 API")
public interface MapApi {

    @Operation(
            summary = "반경 내 Top10 가맹점 검색",
            description = """
                    사용자 위치(위도, 경도)와 반경(km)을 기준으로 DB에 저장된 가맹점 지점 중
                    Top10 가맹점에 해당하는 지점을 최대 10개 반환.
                    반환 순서: Top10 우선순위 → 같은 가맹점 내 거리 오름차순.
                    Top10 가맹점이 DB에 없으면 결과에서 제외됨.
                    """,
            security = {})
    ResponseEntity<ApiResponse<List<NearbySearchResponse.NearbyPlaceItem>>> getNearbyTop10(
            @Parameter(description = "검색 조건 (위도, 경도, 반경(km))", required = true) NearbySearchRequest request);
}
