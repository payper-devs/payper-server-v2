package com.payper.server.merchant.controller;

import com.payper.server.global.response.ApiResponse;
import com.payper.server.merchant.dto.CategoryRequest;
import com.payper.server.merchant.dto.CategoryResponse;
import com.payper.server.merchant.service.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController implements CategoryApi {
    private final CategoryService categoryService;

    /** 카테고리 등록 */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<ApiResponse<Long>> registerCategory(
            @RequestBody @Valid CategoryRequest.RegisterCategory request) {
        Long categoryId = categoryService.registerCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(categoryId));
    }

    /** 카테고리 수정 */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> updateCategory(
            @PathVariable Long categoryId, @RequestBody @Valid CategoryRequest.UpdateCategory request) {
        categoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    /** 카테고리 조회 */
    @GetMapping()
    public ResponseEntity<ApiResponse<List<CategoryResponse.CategoryItem>>> getCategories(
            @RequestParam(required = false) Long parentCategoryId) {
        List<CategoryResponse.CategoryItem> response = categoryService.getCategories(parentCategoryId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
