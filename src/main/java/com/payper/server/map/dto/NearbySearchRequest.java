package com.payper.server.map.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record NearbySearchRequest(
        @NotNull Double latitude,
        @NotNull Double longitude,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) Double radiusKm
) {}
