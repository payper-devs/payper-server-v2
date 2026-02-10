package com.payper.server.merchant.dto;

import com.payper.server.merchant.entity.Category;

public class CategoryResponse {

    /**
     * Category Item
     */
    public record CategoryItem(
            Long id,
            String name,
            Long parentCategoryId
    ) {
        public static CategoryResponse.CategoryItem from(Category category) {
            return new CategoryResponse.CategoryItem(
                    category.getId(),
                    category.getName(),
                    category.getParentCategory() != null ? category.getParentCategory().getId() : null
            );
        }
    }
}