package com.payper.server.merchant.controller;

import com.payper.server.global.response.ApiResponse;
import com.payper.server.post.dto.PostRequest;
import com.payper.server.post.service.PostService;
import com.payper.server.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/merchants")
@RequiredArgsConstructor
public class MerchantController implements MerchantApi {
    private final PostService postService;

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
        return ResponseEntity.status(201).body(ApiResponse.created(postId));
    }
}
