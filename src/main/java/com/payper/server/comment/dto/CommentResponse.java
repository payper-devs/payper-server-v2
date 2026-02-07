package com.payper.server.comment.dto;

import com.payper.server.comment.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponse {

    /**
     * Post에 달린 Comment 리스트
     */
    @Schema(description = "댓글 목록 (커서 기반 페이지네이션)")
    public record CommentList(
            @Schema(description = "댓글 목록")
            List<CommentResponse.CommentItem> comments,
            @Schema(description = "다음 페이지 커서 (다음 페이지가 없으면 null)", example = "25")
            Long nextCursor,
            @Schema(description = "다음 페이지 존재 여부", example = "true")
            boolean hasNext
    ) {
        public static CommentList from(List<Comment> comments, Long nextCursor, boolean hasNext) {
            return new CommentList(
                    comments.stream()
                            .map(CommentResponse.CommentItem::from)
                            .toList(),
                    nextCursor,
                    hasNext
            );
        }
    }

    /**
     * Comment Item
     */
    @Schema(description = "댓글 항목")
    public record CommentItem(
            @Schema(description = "댓글 ID", example = "1")
            Long id,
            @Schema(description = "작성자 이름", example = "홍길동")
            String userName,
            @Schema(description = "부모 댓글 ID (최상위 댓글이면 null)", example = "1")
            Long parentCommentId,
            @Schema(description = "댓글 내용 (삭제 시 '[삭제된 댓글입니다]')", example = "좋은 글이네요!")
            String content,
            @Schema(description = "작성일시")
            LocalDateTime createdAt,
            @Schema(description = "수정일시")
            LocalDateTime updatedAt
    ) {
        public static CommentItem from(Comment comment) {
            return new CommentItem(
                    comment.getId(),
                    comment.getUser().getName(),
                    comment.getParentComment() != null ? comment.getParentComment().getId() : null,
                    comment.isDeleted() ? "[삭제된 댓글입니다]" : comment.getContent(),
                    comment.getCreatedAt(),
                    comment.getUpdatedAt()
            );
        }
    }

    /**
     * 내가 작성한 Comment 리스트
     */
    @Schema(description = "내가 작성한 댓글 목록 (커서 기반 페이지네이션)")
    public record MyCommentList(
            @Schema(description = "댓글 목록")
            List<CommentResponse.MyCommentItem> comments,
            @Schema(description = "다음 페이지 커서 (다음 페이지가 없으면 null)", example = "25")
            Long nextCursor,
            @Schema(description = "다음 페이지 존재 여부", example = "true")
            boolean hasNext
    ) {
        public static MyCommentList from(List<Comment> comments, Long nextCursor, boolean hasNext) {
            return new MyCommentList(
                    comments.stream()
                            .map(CommentResponse.MyCommentItem::from)
                            .toList(),
                    nextCursor,
                    hasNext
            );
        }
    }

    /**
     * 내가 작성한 Comment Item
     */
    @Schema(description = "내가 작성한 댓글 항목")
    public record MyCommentItem(
            @Schema(description = "댓글 ID", example = "1")
            Long id,
            @Schema(description = "게시글 ID", example = "10")
            Long postId,
            @Schema(description = "댓글 내용", example = "좋은 글이네요!")
            String content,
            @Schema(description = "작성일시")
            LocalDateTime createdAt,
            @Schema(description = "수정일시")
            LocalDateTime updatedAt
    ) {
        public static MyCommentItem from(Comment comment) {
            return new MyCommentItem(
                    comment.getId(),
                    comment.getPost().getId(),
                    comment.getContent(),
                    comment.getCreatedAt(),
                    comment.getUpdatedAt()
            );
        }
    }
}
