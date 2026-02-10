package com.payper.server.comment.controller;

import com.payper.server.comment.dto.CommentRequest;
import com.payper.server.comment.dto.CommentResponse;
import com.payper.server.global.response.ApiResponse;
import com.payper.server.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "댓글", description = "댓글 수정/삭제, 내 댓글 조회, 대댓글 조회 API")
public interface CommentApi {

    @Operation(summary = "댓글 수정", description = "작성자만 수정 가능")
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<ApiResponse<Void>> updateComment(
            CustomUserDetails user,
            @Parameter(description = "댓글 ID", example = "1") Long commentId,
            CommentRequest.UpdateComment request
    );

    @Operation(summary = "댓글 삭제", description = "작성자만 삭제 가능. 자식 댓글은 삭제되지 않습니다.")
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<ApiResponse<Void>> deleteComment(
            CustomUserDetails user,
            @Parameter(description = "댓글 ID", example = "1") Long commentId
    );

    @Operation(summary = "내가 쓴 댓글 조회", description = "커서 기반 페이지네이션. 최신순 정렬. 삭제된 댓글은 제외됩니다.")
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<ApiResponse<CommentResponse.MyCommentList>> getMyComments(
            CustomUserDetails user,
            @Parameter(description = "마지막 조회 댓글 ID (첫 요청 시 생략)") Long cursorId,
            @Parameter(description = "조회 개수", example = "20") int size
    );

    @Operation(summary = "대댓글 조회", description = "부모 댓글의 대댓글을 커서 기반으로 조회합니다. 삭제된 댓글은 제외됩니다.", security = {})
    ResponseEntity<ApiResponse<CommentResponse.CommentList>> getReplies(
            @Parameter(description = "부모 댓글 ID", example = "1") Long parentId,
            @Parameter(description = "마지막 조회 댓글 ID (첫 요청 시 생략)") Long cursorId,
            @Parameter(description = "조회 개수", example = "20") int size
    );
}
