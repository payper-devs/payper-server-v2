package com.payper.server.map.service;

import com.payper.server.map.dto.NearbySearchResponse;
import com.payper.server.merchant.entity.MerchantLocation;
import com.payper.server.merchant.repository.MerchantLocationRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NearbyMerchantService {

    private static final int MAX_RESULTS = 10;
    private static final double EARTH_RADIUS_KM = 6371.0;

    private static final List<String> TOP10_MERCHANTS =
            List.of("스타벅스", "맥도날드", "CGV", "올리브영", "다이소", "GS25", "CU", "이디야커피", "파리바게뜨", "교촌치킨");

    private final MerchantLocationRepository merchantLocationRepository;

    public List<NearbySearchResponse.NearbyPlaceItem> searchNearbyTop10(
            double userLat, double userLng, double radiusKm) {

        // 위도 1도는 약 111km, 경도 1도는 cos(위도) * 111km
        double latChange = radiusKm / 111.0;
        double lonChange = radiusKm / (111.0 * Math.cos(Math.toRadians(userLat)));

        double minLat = userLat - latChange;
        double maxLat = userLat + latChange;
        double minLon = userLng - lonChange;
        double maxLon = userLng + lonChange;

        Map<String, List<MerchantLocation>> byMerchant =
                merchantLocationRepository
                        .findByMerchantNamesInBoundingBox(TOP10_MERCHANTS, minLat, maxLat, minLon, maxLon)
                        .stream()
                        .collect(Collectors.groupingBy(ml -> ml.getMerchant().getName()));

        List<NearbySearchResponse.NearbyPlaceItem> results = new ArrayList<>();

        for (int i = 0; i < TOP10_MERCHANTS.size() && results.size() < MAX_RESULTS; i++) {
            String merchantName = TOP10_MERCHANTS.get(i);
            List<MerchantLocation> locations = byMerchant.get(merchantName);
            if (locations == null) continue;

            int rank = i + 1;
            int remaining = MAX_RESULTS - results.size();

            locations.stream()
                    .map(ml -> new Object() {
                        final MerchantLocation location = ml;
                        final double distance = haversine(userLat, userLng, ml.getLatitude(), ml.getLongitude());
                    })
                    .filter(item -> item.distance <= radiusKm)
                    .sorted(Comparator.comparingDouble(item -> item.distance))
                    .limit(remaining)
                    .map(item -> new NearbySearchResponse.NearbyPlaceItem(
                            rank,
                            merchantName,
                            item.location.getPlaceName(),
                            item.location.getLatitude(),
                            item.location.getLongitude(),
                            item.distance))
                    .forEach(results::add);
        }

        return results;
    }

    /** Haversine 공식으로 두 좌표 간 거리(km)를 계산한다. */
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}
