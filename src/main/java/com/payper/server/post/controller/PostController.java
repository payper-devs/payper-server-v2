package com.payper.server.post.controller;

import com.payper.server.comment.dto.CommentRequest;
import com.payper.server.comment.dto.CommentResponse;
import com.payper.server.comment.service.CommentService;
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
    private final CommentService commentService;

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

    /**
     * 댓글 작성
     * is inactive = false, is deleted = false 상태의 post에만 댓글을 작성할 수 있음
     *
     * 부모 댓글이 삭제되어도 대댓글 작성 허용
     */
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<Long>> createComment(
            // TODO @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long postId,
            @RequestBody @Valid CommentRequest.CreateComment request
    ) {
        Long commentId = commentService.createComment(1L, postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(commentId));
    }

    /**
     * 게시글 댓글 조회
     *
     * 부모 댓글로 페이지네이션
     * 주의) 부모 댓글이 삭제되어도 자식 댓글이 남아있으면 [삭제된 댓글입니다]로 제공
     */
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse.CommentList>> getPostComments(
            @PathVariable Long postId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "20") int size
    ) {
        CommentResponse.CommentList response = commentService.getPostComments(postId, cursorId, size);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}