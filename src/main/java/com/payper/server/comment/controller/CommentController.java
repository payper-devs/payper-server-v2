package com.payper.server.comment.controller;

import com.payper.server.comment.dto.CommentRequest;
import com.payper.server.comment.dto.CommentResponse;
import com.payper.server.comment.service.CommentService;
import com.payper.server.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    /**
     * 댓글 작성
     * is inactive = false, is deleted = false 상태의 post에만 댓글을 작성할 수 있음
     *
     * 부모 댓글이 삭제되어도 대댓글 작성 허용
     */
    @PostMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<Long>> createComment(
            // TODO @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long postId,
            @RequestBody @Valid CommentRequest.CreateComment request
    ) {
        Long commentId = commentService.createComment(1L, postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(commentId));
    }

    /**
     * 댓글 수정
     * 작성자만 수정 가능
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            // TODO @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long commentId,
            @RequestBody @Valid CommentRequest.UpdateComment request
    ) {
        commentService.updateComment(1L, commentId, request);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    /**
     * 댓글 삭제
     * 작성자만 삭제 가능
     *
     * 자식 댓글은 삭제하지 않음
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            // TODO @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long commentId) {

        commentService.deleteComment(1L, commentId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    /**
     * 내가 쓴 댓글 조회
     * 
     * 무한 스크롤 방식
     * 
     * 정렬: 최신 순
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CommentResponse.MyCommentList>> getMyComments(
            // TODO @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "20") int size
    ) {
        CommentResponse.MyCommentList response = commentService.getMyComments(1L, cursorId, size);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * 게시글 댓글 조회
     *
     * TODO 부모 댓글은 페이지네이션, 대댓글은 전체 조회
     */
    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<List<CommentResponse.CommentItem>>> getPostComments(@PathVariable Long postId) {
        List<CommentResponse.CommentItem> response = commentService.getPostComments(postId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}