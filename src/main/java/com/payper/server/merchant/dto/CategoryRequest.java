package com.payper.server.merchant.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public class CategoryRequest {

    /**
     * 카테고리 등록 DTO
     */
    public record RegisterCategory(
            @NotBlank(message = "카테고리명을 적어주세요. 예) 카페")
            String name,

            @Nullable
            Long parentCategoryId
    ) {}

    /**
     * 카테고리 수정 DTO
     */
    public record UpdateCategory(
            @NotBlank(message = "카테고리명을 적어주세요. 예) 카페")
            String name,

            @Nullable
            Long parentCategoryId
    ) {}
}