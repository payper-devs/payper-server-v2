package com.payper.server.merchant.repository;

import com.payper.server.merchant.entity.MerchantLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantLocationRepository extends JpaRepository<MerchantLocation, Long> {

    @Query("""
        select ml from MerchantLocation ml
        join fetch ml.merchant m
        where m.name in :merchantNames
    """)
    List<MerchantLocation> findByMerchantNames(@Param("merchantNames") List<String> merchantNames);
}
