package com.payper.server.post.controller;

import com.payper.server.global.response.ApiResponse;
import com.payper.server.post.dto.PostRequest;
import com.payper.server.post.dto.PostResponse;
import com.payper.server.post.dto.PostSortType;
import com.payper.server.post.entity.PostType;
import com.payper.server.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    /**
     * 게시글 작성
     * 가맹점에 대해 글을 작성함
     * 가맹점 리스트에서 가맹점을 선택해서 해당 가맹점의 id를 넘겨 받음
     * TODO 가맹점이 없을 때는 어떻게 해야할까?
     *
     * 가입된 사용자만 글을 작성할 수 있음
     */
    @PostMapping("/merchants/{merchantId}") // TODO: 흠 RESTFUL한 URL은 아닌 것 같음, posts가 뒤로 가는 게 맞는 것 같음
    public ResponseEntity<ApiResponse<Long>> createPost(
            // TODO @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long merchantId,
            @RequestBody @Valid PostRequest.CreatePost request
    ) {
        Long postId = postService.createPost(1L, merchantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(postId));
    }

    /**
     * 게시글 수정
     * 작성자만 수정 가능
     */
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updatePost(
            // TODO @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long postId,
            @RequestBody @Valid PostRequest.UpdatePost request
    ) {
        postService.updatePost(1L, postId, request);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    /**
     * 게시글 삭제
     * 작성자만 삭제 가능
     * TODO 댓글도 같이 soft delete
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            // TODO @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long postId
    ) {
        postService.deletePost(1L, postId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    /**
     * 단일 게시글 조회
     *
     * 삭제되지 않은 글만 조회함
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse.PostDetail>> getPostDetail(@PathVariable Long postId) {
        PostResponse.PostDetail response = postService.getPostDetail(postId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * 게시글 리스트 조회
     *
     * 필터링 조건
     * 가맹점, postType
     *
     * 정렬 조건
     * 생성 순, 댓글 수, 좋아요 수, 조회 수
     *
     * 페이지네이션
     */
    @GetMapping()
    public ResponseEntity<ApiResponse<Page<PostResponse.PostList>>> getPosts(
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) PostType type,
            @RequestParam(defaultValue = "POSTING_DATE") PostSortType sort,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, sort.toSort(direction));
        Page<PostResponse.PostList> response = postService.getPosts(merchantId, type, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}