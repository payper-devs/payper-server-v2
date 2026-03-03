package com.payper.server.merchant.repository;

import com.payper.server.merchant.entity.MerchantLocation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantLocationRepository extends JpaRepository<MerchantLocation, Long> {

    @Query("""
    select ml from MerchantLocation ml
    join fetch ml.merchant m
    where m.name in :merchantNames
    and ml.latitude between :minLat and :maxLat
    and ml.longitude between :minLon and :maxLon
""")
    List<MerchantLocation> findByMerchantNamesInBoundingBox(
            @Param("merchantNames") List<String> merchantNames,
            @Param("minLat") double minLat,
            @Param("maxLat") double maxLat,
            @Param("minLon") double minLon,
            @Param("maxLon") double maxLon);
}
