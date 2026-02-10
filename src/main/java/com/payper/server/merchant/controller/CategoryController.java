package com.payper.server.merchant.controller;

import com.payper.server.global.response.ApiResponse;
import com.payper.server.merchant.dto.CategoryRequest;
import com.payper.server.merchant.dto.CategoryResponse;
import com.payper.server.merchant.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    /**
     * 카테고리 등록
     * 관리자만 등록 가능
     * depth는 최대 2
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<ApiResponse<Long>> registerCategory(
            @RequestBody @Valid CategoryRequest.RegisterCategory request
    ) {
        Long categoryId = categoryService.registerCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(categoryId));
    }

    /**
     * 카테고리 수정 (depth는 수정할 수 없음)
     * 관리자만 수정 가능
     * 부모 카테고리 -> 이름만 변경 가능
     * 자식 카테고리 -> 이름, 부모 변경 가능
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody @Valid CategoryRequest.UpdateCategory request
    ) {
        categoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    /**
     * 카테고리 조회
     *
     * 필터링 조건
     * 부모 카테고리
     * 파라미터 안 넣으면 부모 카테고리만 보임
     *
     * 정렬 조건
     * 카테고리명, 오름차순
     */
    @GetMapping()
    public ResponseEntity<ApiResponse<List<CategoryResponse.CategoryItem>>> getCategories(
            @RequestParam(required = false) Long parentCategoryId
    ) {
        List<CategoryResponse.CategoryItem> response = categoryService.getCategories(parentCategoryId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}