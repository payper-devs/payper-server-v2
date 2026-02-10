package com.payper.server.merchant.controller;

import com.payper.server.global.response.ApiResponse;
import com.payper.server.merchant.dto.MerchantRequest;
import com.payper.server.merchant.dto.MerchantResponse;
import com.payper.server.post.dto.PostRequest;
import com.payper.server.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "가맹점", description = "가맹점 등록/수정/조회 및 게시글 작성 API")
public interface MerchantApi {

    @Operation(summary = "가맹점 등록", description = "관리자만 등록 가능. 카테고리를 선택하여 가맹점을 등록합니다.")
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<ApiResponse<Long>> registerMerchant(
            @RequestBody MerchantRequest.RegisterMerchant request
    );

    @Operation(summary = "가맹점 수정", description = "관리자만 수정 가능")
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<ApiResponse<Void>> updateMerchant(
            @Parameter(description = "가맹점 ID", required = true, example = "1") Long merchantId,
            @RequestBody MerchantRequest.UpdateMerchant request
    );

    @Operation(summary = "가맹점 조회", description = "카테고리 ID로 필터링 가능. 가맹점명 오름차순 정렬.", security = {})
    ResponseEntity<ApiResponse<List<MerchantResponse.MerchantItem>>> getMerchants(
            @Parameter(description = "카테고리 ID 필터", required = false) Long categoryId
    );

    @Operation(summary = "게시글 작성", description = "가맹점에 대한 게시글을 작성합니다.")
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<ApiResponse<Long>> createPost(
            CustomUserDetails user,
            @Parameter(description = "가맹점 ID", example = "1", required = true) Long merchantId,
            @RequestBody PostRequest.CreatePost request
    );
}
