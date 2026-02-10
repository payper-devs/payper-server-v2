package com.payper.server.post.controller;

import com.payper.server.comment.dto.CommentRequest;
import com.payper.server.comment.dto.CommentResponse;
import com.payper.server.global.response.ApiResponse;
import com.payper.server.post.dto.PostRequest;
import com.payper.server.post.dto.PostResponse;
import com.payper.server.post.dto.PostSortType;
import com.payper.server.post.entity.PostType;
import com.payper.server.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

@Tag(name = "게시글", description = "게시글 CRUD 및 댓글 작성/조회 API")
public interface PostApi {

    @Operation(summary = "게시글 수정", description = "작성자만 수정 가능")
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<ApiResponse<Void>> updatePost(
            CustomUserDetails user,
            @Parameter(description = "게시글 ID", example = "1") Long postId,
            PostRequest.UpdatePost request
    );

    @Operation(summary = "게시글 삭제", description = "작성자만 삭제 가능 (소프트 삭제)")
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<ApiResponse<Void>> deletePost(
            CustomUserDetails user,
            @Parameter(description = "게시글 ID", example = "1") Long postId
    );

    @Operation(summary = "게시글 상세 조회", description = "삭제되지 않은 게시글만 조회 가능", security = {})
    ResponseEntity<ApiResponse<PostResponse.PostDetail>> getPostDetail(
            @Parameter(description = "게시글 ID", example = "1") Long postId
    );

    @Operation(summary = "게시글 목록 조회", description = "가맹점/타입 필터링, 정렬, 오프셋 기반 페이지네이션을 지원합니다.", security = {})
    ResponseEntity<ApiResponse<Page<PostResponse.PostList>>> getPosts(
            @Parameter(description = "가맹점 ID 필터") Long merchantId,
            @Parameter(description = "게시글 타입 필터 (BENEFIT, QUESTION, ETC)") PostType type,
            @Parameter(description = "정렬 기준 (POSTING_DATE, COMMENT_COUNT, LIKE_COUNT, VIEW_COUNT)") PostSortType sort,
            @Parameter(description = "정렬 방향") Sort.Direction direction,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") int size
    );

    @Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다. 대댓글은 parentCommentId를 지정합니다.")
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<ApiResponse<Long>> createComment(
            CustomUserDetails user,
            @Parameter(description = "게시글 ID", example = "1") Long postId,
            CommentRequest.CreateComment request
    );

    @Operation(summary = "게시글 댓글 조회", description = "부모 댓글 기준 커서 페이지네이션. 삭제된 부모 댓글도 자식이 있으면 '[삭제된 댓글입니다]'로 표시됩니다.", security = {})
    ResponseEntity<ApiResponse<CommentResponse.CommentList>> getPostComments(
            @Parameter(description = "게시글 ID", example = "1") Long postId,
            @Parameter(description = "마지막 조회 댓글 ID (첫 요청 시 생략)") Long cursorId,
            @Parameter(description = "조회 개수", example = "20") int size
    );
}
