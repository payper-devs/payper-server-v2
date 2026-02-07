package com.payper.server.comment.controller;

import com.payper.server.comment.dto.CommentRequest;
import com.payper.server.comment.dto.CommentResponse;
import com.payper.server.comment.service.CommentService;
import com.payper.server.global.response.ApiResponse;
import com.payper.server.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "댓글", description = "댓글 수정/삭제, 내 댓글 조회, 대댓글 조회 API")
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    /**
     * 댓글 수정
     * 작성자만 수정 가능
     */
    @Operation(summary = "댓글 수정", description = "작성자만 수정 가능")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "댓글 ID", example = "1") @PathVariable Long commentId,
            @RequestBody @Valid CommentRequest.UpdateComment request
    ) {
        commentService.updateComment(user.getId(), commentId, request);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    /**
     * 댓글 삭제
     * 작성자만 삭제 가능
     *
     * 자식 댓글은 삭제하지 않음
     */
    @Operation(summary = "댓글 삭제", description = "작성자만 삭제 가능. 자식 댓글은 삭제되지 않습니다.")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "댓글 ID", example = "1") @PathVariable Long commentId) {

        commentService.deleteComment(user.getId(), commentId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    /**
     * 내가 쓴 댓글 조회
     *
     * 무한 스크롤 방식
     *
     * 정렬: 최신 순
     */
    @Operation(summary = "내가 쓴 댓글 조회", description = "커서 기반 페이지네이션. 최신순 정렬. 삭제된 댓글은 제외됩니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CommentResponse.MyCommentList>> getMyComments(
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "마지막 조회 댓글 ID (첫 요청 시 생략)") @RequestParam(required = false) Long cursorId,
            @Parameter(description = "조회 개수", example = "20") @RequestParam(defaultValue = "20") int size
    ) {
        CommentResponse.MyCommentList response = commentService.getMyComments(user.getId(), cursorId, size);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * 자식 댓글 조회
     */
    @Operation(summary = "대댓글 조회", description = "부모 댓글의 대댓글을 커서 기반으로 조회합니다. 삭제된 댓글은 제외됩니다.", security = {})
    @GetMapping("/{parentId}/replies")
    public ResponseEntity<ApiResponse<CommentResponse.CommentList>> getReplies(
            @Parameter(description = "부모 댓글 ID", example = "1") @PathVariable Long parentId,
            @Parameter(description = "마지막 조회 댓글 ID (첫 요청 시 생략)") @RequestParam(required = false) Long cursorId,
            @Parameter(description = "조회 개수", example = "20") @RequestParam(defaultValue = "20") int size
    ) {
        CommentResponse.CommentList response = commentService.getReplies(parentId, cursorId, size);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
