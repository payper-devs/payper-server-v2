package com.payper.server.map.dto;

public class NearbySearchResponse {

    public record NearbyPlaceItem(
            int merchantRank,
            String merchantName,
            String placeName,
            double latitude,
            double longitude,
            double distanceKm
    ) {}
}
