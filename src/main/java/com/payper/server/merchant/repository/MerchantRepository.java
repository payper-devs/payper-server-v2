package com.payper.server.merchant.repository;

import com.payper.server.merchant.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    @Query("""
        select m from Merchant m
        join fetch m.category
        where (:categoryId IS NULL or m.category.id = :categoryId)
        order by m.name asc
    """)
    List<Merchant> findMerchants(@Param("categoryId") Long categoryId);
}
