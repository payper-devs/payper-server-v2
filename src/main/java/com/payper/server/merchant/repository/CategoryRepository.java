package com.payper.server.merchant.repository;

import com.payper.server.merchant.entity.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    boolean existsByIdAndParentCategoryIsNull(Long id);

    List<Category> findByParentCategoryIsNullOrderByNameAsc();

    List<Category> findByParentCategoryIdOrderByNameAsc(Long parentCategoryId);
}
