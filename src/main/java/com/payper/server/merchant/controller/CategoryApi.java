package com.payper.server.merchant.controller;

import com.payper.server.global.response.ApiResponse;
import com.payper.server.merchant.dto.CategoryRequest;
import com.payper.server.merchant.dto.CategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "카테고리", description = "카테고리 등록/수정/조회 API")
public interface CategoryApi {

    @Operation(summary = "카테고리 등록", description = "관리자만 등록 가능. depth는 최대 2.")
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<ApiResponse<Long>> registerCategory(
            @RequestBody CategoryRequest.RegisterCategory request
    );

    @Operation(summary = "카테고리 수정", description = "관리자만 수정 가능. 부모 카테고리는 이름만, 자식 카테고리는 이름과 부모 변경 가능.")
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<ApiResponse<Void>> updateCategory(
            @Parameter(description = "카테고리 ID", required = true, example = "1") Long categoryId,
            @RequestBody CategoryRequest.UpdateCategory request
    );

    @Operation(summary = "카테고리 조회", description = "부모 카테고리 ID로 필터링 가능. 파라미터 미지정 시 부모 카테고리만 조회. 카테고리명 오름차순 정렬.", security = {})
    ResponseEntity<ApiResponse<List<CategoryResponse.CategoryItem>>> getCategories(
            @Parameter(description = "부모 카테고리 ID 필터", required = false) Long parentCategoryId
    );
}