package com.payper.server.merchant.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MerchantLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    /**
     * 지점명 (예: 스타벅스 강남점)
     */
    @Column(nullable = false)
    private String placeName;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    public static MerchantLocation create(Merchant merchant, String placeName,
                                          Double latitude, Double longitude) {
        return MerchantLocation.builder()
                .merchant(merchant)
                .placeName(placeName)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
