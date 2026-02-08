package com.payper.server.merchant.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MerchantRequest {

    /**
     * 가맹점 등록 DTO
     */
    public record RegisterMerchant(
            @NotBlank(message = "가맹점 명을 적어주세요. 예) 스타벅스")
            String name,

            @NotNull(message = "카테고리 ID는 필수입니다.")
            Long categoryId,

            @Nullable
            String imageUrl
    ) {}

    /**
     * 가맹점 수정 DTO
     */
    public record UpdateMerchant(
            @NotBlank(message = "가맹점 명을 적어주세요. 예) 스타벅스")
            String name,

            @Nullable
            String imageUrl
    ) {}
}