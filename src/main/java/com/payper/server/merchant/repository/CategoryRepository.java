package com.payper.server.merchant.repository;

import com.payper.server.merchant.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    @Query("""
        select c
        from Category c
        where (:parentCategoryId IS NULL and c.parentCategory IS NULL)
           or (:parentCategoryId IS NOT NULL and c.parentCategory.id = :parentCategoryId)
        order by c.name asc
    """)
    List<Category> findByParentCategoryId(@Param("parentCategoryId") Long parentCategoryId);
}
