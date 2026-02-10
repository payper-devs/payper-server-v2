package com.payper.server.merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MerchantRequest {

    /**
     * 가맹점 등록 DTO
     */
    @Schema(description = "가맹점 등록 요청")
    public record RegisterMerchant(
            @Schema(description = "가맹점명", example = "스타벅스")
            @NotBlank(message = "가맹점 명을 적어주세요. 예) 스타벅스")
            String name,

            @Schema(description = "카테고리 ID", example = "1")
            @NotNull(message = "카테고리 ID는 필수입니다.")
            Long categoryId,

            @Schema(description = "가맹점 이미지 URL", example = "https://example.com/image.png", nullable = true)
            @Nullable
            String imageUrl
    ) {}

    /**
     * 가맹점 수정 DTO
     */
    @Schema(description = "가맹점 수정 요청")
    public record UpdateMerchant(
            @Schema(description = "가맹점명", example = "스타벅스")
            @NotBlank(message = "가맹점 명을 적어주세요. 예) 스타벅스")
            String name,

            @Schema(description = "가맹점 이미지 URL", example = "https://example.com/image.png", nullable = true)
            @Nullable
            String imageUrl
    ) {}
}