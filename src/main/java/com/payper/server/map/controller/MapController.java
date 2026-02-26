package com.payper.server.map.controller;

import com.payper.server.global.response.ApiResponse;
import com.payper.server.map.dto.NearbySearchRequest;
import com.payper.server.map.dto.NearbySearchResponse;
import com.payper.server.map.service.NearbyMerchantService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/map")
@RequiredArgsConstructor
public class MapController {

    private final NearbyMerchantService nearbyMerchantService;

    @GetMapping("/nearby-top10")
    public ApiResponse<List<NearbySearchResponse.NearbyPlaceItem>> getNearbyTop10(
            @Valid @ModelAttribute NearbySearchRequest request) {

        List<NearbySearchResponse.NearbyPlaceItem> result =
                nearbyMerchantService.searchNearbyTop10(request.latitude(), request.longitude(), request.radiusKm());
        return ApiResponse.ok(result);
    }
}
