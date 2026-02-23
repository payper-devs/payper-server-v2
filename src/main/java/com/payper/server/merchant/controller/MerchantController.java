package com.payper.server.merchant.controller;

import com.payper.server.global.response.ApiResponse;
import com.payper.server.merchant.dto.MerchantRequest;
import com.payper.server.merchant.dto.MerchantResponse;
import com.payper.server.merchant.service.MerchantService;
import com.payper.server.post.dto.PostRequest;
import com.payper.server.post.service.PostService;
import com.payper.server.security.CustomUserDetails;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/merchants")
@RequiredArgsConstructor
public class MerchantController implements MerchantApi {
    private final MerchantService merchantService;
    private final PostService postService;

    /** 가맹점 등록 */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<ApiResponse<Long>> registerMerchant(
            @RequestBody @Valid MerchantRequest.RegisterMerchant request) {
        Long merchantId = merchantService.registerMerchant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(merchantId));
    }

    /** 가맹점 수정 */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{merchantId}")
    public ResponseEntity<ApiResponse<Void>> updateMerchant(
            @PathVariable Long merchantId, @RequestBody @Valid MerchantRequest.UpdateMerchant request) {
        merchantService.updateMerchant(merchantId, request);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    /** 가맹점 조회 */
    @GetMapping()
    public ResponseEntity<ApiResponse<List<MerchantResponse.MerchantItem>>> getMerchants(
            @RequestParam(required = false) Long categoryId) {
        List<MerchantResponse.MerchantItem> response = merchantService.getMerchants(categoryId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /** 게시글 작성 TODO 가맹점이 없을 때는 어떻게 해야할까? */
    @PostMapping("/{merchantId}/posts")
    public ResponseEntity<ApiResponse<Long>> createPost(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long merchantId,
            @RequestBody @Valid PostRequest.CreatePost request) {
        Long postId = postService.createPost(user.getId(), merchantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(postId));
    }
}
