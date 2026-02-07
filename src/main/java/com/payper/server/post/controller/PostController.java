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
import com.payper.server.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "게시글", description = "게시글 CRUD 및 댓글 작성/조회 API")
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
    @Operation(summary = "게시글 수정", description = "작성자만 수정 가능")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updatePost(
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "게시글 ID", example = "1") @PathVariable Long postId,
            @RequestBody @Valid PostRequest.UpdatePost request
    ) {
        postService.updatePost(user.getId(), postId, request);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    /**
     * 게시글 삭제
     * 작성자만 삭제 가능
     */
    @Operation(summary = "게시글 삭제", description = "작성자만 삭제 가능 (소프트 삭제)")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "게시글 ID", example = "1") @PathVariable Long postId
    ) {
        postService.deletePost(user.getId(), postId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    /**
     * 단일 게시글 조회
     *
     * 삭제되지 않은 글만 조회함
     */
    @Operation(summary = "게시글 상세 조회", description = "삭제되지 않은 게시글만 조회 가능", security = {})
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse.PostDetail>> getPostDetail(
            @Parameter(description = "게시글 ID", example = "1") @PathVariable Long postId
    ) {
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
    @Operation(summary = "게시글 목록 조회", description = "가맹점/타입 필터링, 정렬, 오프셋 기반 페이지네이션을 지원합니다.", security = {})
    @GetMapping()
    public ResponseEntity<ApiResponse<Page<PostResponse.PostList>>> getPosts(
            @Parameter(description = "가맹점 ID 필터") @RequestParam(required = false) Long merchantId,
            @Parameter(description = "게시글 타입 필터 (BENEFIT, QUESTION, ETC)") @RequestParam(required = false) PostType type,
            @Parameter(description = "정렬 기준 (POSTING_DATE, COMMENT_COUNT, LIKE_COUNT, VIEW_COUNT)") @RequestParam(defaultValue = "POSTING_DATE") PostSortType sort,
            @Parameter(description = "정렬 방향") @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size
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
    @Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다. 대댓글은 parentCommentId를 지정합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<Long>> createComment(
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "게시글 ID", example = "1") @PathVariable Long postId,
            @RequestBody @Valid CommentRequest.CreateComment request
    ) {
        Long commentId = commentService.createComment(user.getId(), postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(commentId));
    }

    /**
     * 게시글 댓글 조회
     *
     * 부모 댓글로 페이지네이션
     * 주의) 부모 댓글이 삭제되어도 자식 댓글이 남아있으면 [삭제된 댓글입니다]로 제공
     */
    @Operation(summary = "게시글 댓글 조회", description = "부모 댓글 기준 커서 페이지네이션. 삭제된 부모 댓글도 자식이 있으면 '[삭제된 댓글입니다]'로 표시됩니다.", security = {})
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse.CommentList>> getPostComments(
            @Parameter(description = "게시글 ID", example = "1") @PathVariable Long postId,
            @Parameter(description = "마지막 조회 댓글 ID (첫 요청 시 생략)") @RequestParam(required = false) Long cursorId,
            @Parameter(description = "조회 개수", example = "20") @RequestParam(defaultValue = "20") int size
    ) {
        CommentResponse.CommentList response = commentService.getPostComments(postId, cursorId, size);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
