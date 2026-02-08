package com.payper.server.merchant.controller;

import com.payper.server.global.response.ApiResponse;
import com.payper.server.merchant.dto.MerchantRequest;
import com.payper.server.merchant.dto.MerchantResponse;
import com.payper.server.merchant.service.MerchantService;
import com.payper.server.post.dto.PostRequest;
import com.payper.server.post.service.PostService;
import com.payper.server.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/merchants")
@RequiredArgsConstructor
public class MerchantController {
    private final MerchantService merchantService;
    private final PostService postService;

    /**
     * 가맹점 등록
     *
     * 관리자만 등록 가능
     * 카테고리 리스트에서 카테고리를 선택해서 해당 카테고리의 id를 넘겨 받음
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<ApiResponse<Long>> registerMerchant(
            @RequestBody @Valid MerchantRequest.RegisterMerchant request
    ) {
        Long merchantId = merchantService.registerMerchant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(merchantId));
    }

    /**
     * 가맹점 수정
     * 관리자만 수정 가능
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{merchantId}")
    public ResponseEntity<ApiResponse<Void>> updateMerchant(
            @PathVariable Long merchantId,
            @RequestBody @Valid MerchantRequest.UpdateMerchant request
    ) {
        merchantService.updateMerchant(merchantId, request);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    /**
     * 가맹점 조회
     *
     * 필터링 조건
     * 카테고리
     *
     * 정렬 조건
     * 가맹점명, 오름차순
     */
    @GetMapping()
    public ResponseEntity<ApiResponse<List<MerchantResponse.MerchantItem>>> getMerchants(
            @RequestParam(required = false) Long categoryId
    ) {
        List<MerchantResponse.MerchantItem> response = merchantService.getMerchants(categoryId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * 게시글 작성
     * 가맹점에 대해 글을 작성함
     * 가맹점 리스트에서 가맹점을 선택해서 해당 가맹점의 id를 넘겨 받음
     * TODO 가맹점이 없을 때는 어떻게 해야할까?
     *
     * 가입된 사용자만 글을 작성할 수 있음
     */
    @PostMapping("/{merchantId}/posts")
    public ResponseEntity<ApiResponse<Long>> createPost(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long merchantId,
            @RequestBody @Valid PostRequest.CreatePost request
    ) {
        Long postId = postService.createPost(user.getId(), merchantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(postId));
    }
}