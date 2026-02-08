package com.payper.server.merchant.dto;

import com.payper.server.merchant.entity.Merchant;

public class MerchantResponse {

    /**
     * Merchant Item
     */
    public record MerchantItem(
            Long id,
            String name,
            String imageUrl,
            Long categoryId,
            String categoryName
    ) {
        public static MerchantItem from(Merchant merchant) {
            return new MerchantItem(
                    merchant.getId(),
                    merchant.getName(),
                    merchant.getImageUrl(),
                    merchant.getCategory().getId(),
                    merchant.getCategory().getName()
            );
        }
    }
}