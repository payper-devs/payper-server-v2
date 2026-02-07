package com.payper.server.merchant.controller;

import com.payper.server.global.response.ApiResponse;
import com.payper.server.post.dto.PostRequest;
import com.payper.server.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "가맹점", description = "가맹점 관련 API")
public interface MerchantApi {

    @Operation(summary = "게시글 작성", description = "가맹점에 대한 게시글을 작성합니다.")
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<ApiResponse<Long>> createPost(
            CustomUserDetails user,
            @Parameter(description = "가맹점 ID", example = "1") Long merchantId,
            PostRequest.CreatePost request
    );
}
