package com.payper.server.merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public class CategoryRequest {

    /**
     * 카테고리 등록 DTO
     */
    @Schema(description = "카테고리 등록 요청")
    public record RegisterCategory(
            @Schema(description = "카테고리명", example = "카페")
            @NotBlank(message = "카테고리명을 적어주세요. 예) 카페")
            String name,

            @Schema(description = "부모 카테고리 ID (하위 카테고리 등록 시)", example = "1", nullable = true)
            @Nullable
            Long parentCategoryId
    ) {}

    /**
     * 카테고리 수정 DTO
     */
    @Schema(description = "카테고리 수정 요청")
    public record UpdateCategory(
            @Schema(description = "카테고리명", example = "카페")
            @NotBlank(message = "카테고리명을 적어주세요. 예) 카페")
            String name,

            @Schema(description = "부모 카테고리 ID (하위 카테고리의 부모 변경 시)", example = "1", nullable = true)
            @Nullable
            Long parentCategoryId
    ) {}
}