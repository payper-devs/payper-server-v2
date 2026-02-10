package com.payper.server.merchant.service;

import com.payper.server.global.exception.ApiException;
import com.payper.server.global.response.ErrorCode;
import com.payper.server.merchant.dto.CategoryRequest;
import com.payper.server.merchant.dto.CategoryResponse;
import com.payper.server.merchant.entity.Category;
import com.payper.server.merchant.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 카테고리 등록
     */
    @Transactional
    public Long registerCategory(CategoryRequest.RegisterCategory request) {
        // 존재하는 카테고리명인지 체크
        if (categoryRepository.existsByName(request.name())) {
            throw new ApiException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        Category parentCategory = null;
        if (request.parentCategoryId() != null) {
            parentCategory = getValidatedParentCategory(request.parentCategoryId());

            if (parentCategory.isDepth2()) {
                throw new ApiException(ErrorCode.CATEGORY_DEPTH_EXCEEDED);
            }
        }

        // 카테고리 등록
        Category category = Category.register(request.name(), parentCategory);

        try {
            categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        return category.getId();
    }

    // 요청 body로 받은 parent category id 검증
    private Category getValidatedParentCategory(Long parentCategoryId) {
        // 부모 카테고리가 존재하는 카테고리인지 확인
        Category parentCategory = categoryRepository.findById(parentCategoryId)
                .orElseThrow(() -> new ApiException(ErrorCode.CATEGORY_NOT_FOUND));

        return parentCategory;
    }

    /**
     * 카테고리 수정
     */
    @Transactional
    public void updateCategory(Long categoryId, CategoryRequest.UpdateCategory request) {
        // 카테고리 조회
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApiException(ErrorCode.CATEGORY_NOT_FOUND));

        // 존재하는 카테고리명인지 체크 (나 제외)
        if (categoryRepository.existsByNameAndIdNot(request.name(), categoryId)) {
            throw new ApiException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        boolean isParentCategory = category.isRoot();

        // 부모 카테고리인 경우
        if (isParentCategory) {
            // 부모는 자식이 될 수 없음
            if (request.parentCategoryId() != null) {
                throw new ApiException(ErrorCode.PARENT_CATEGORY_CANNOT_HAVE_PARENT);
            }

            category.updateName(request.name());
            return;
        }

        // 자식 카테고리인 경우 1. 이름만 바꾸는 경우
        if (request.parentCategoryId() == null) {
            category.updateName(request.name());
            return;
        }

        // 자식 카테고리인 경우 2. 이름, 부모 모두 바꾸는 경우
        // 변경하고 싶은 부모 카테고리가 존재하는 카테고리인지 확인
        Category newParentCategory = categoryRepository.findById(request.parentCategoryId())
                .orElseThrow(() -> new ApiException(ErrorCode.CATEGORY_NOT_FOUND));

        // 자기 자신 불가
        if (newParentCategory.getId().equals(categoryId)) {
            throw new ApiException(ErrorCode.CATEGORY_CANNOT_BE_SELF_PARENT);
        }

        // 부모는 반드시 root여야 함
        if (newParentCategory.isDepth2()) {
            throw new ApiException(ErrorCode.CATEGORY_DEPTH_EXCEEDED);
        }

        category.updateNameAndParentCategory(request.name(), newParentCategory);
    }

    /**
     * 카테고리 리스트 조회
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse.CategoryItem> getCategories(Long parentCategoryId) {
        List<Category> categories;
        if (parentCategoryId == null) {
            categories = categoryRepository.findByParentCategoryIsNullOrderByNameAsc();
        } else {
            if (!categoryRepository.existsByIdAndParentCategoryIsNull(parentCategoryId)) {
                throw new ApiException(ErrorCode.CATEGORY_NOT_FOUND);
            }
            categories = categoryRepository.findByParentCategoryIdOrderByNameAsc(parentCategoryId);
        }

        return categories.stream()
                .map(CategoryResponse.CategoryItem::from)
                .toList();
    }
}