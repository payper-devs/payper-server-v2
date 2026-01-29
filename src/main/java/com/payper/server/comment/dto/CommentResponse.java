package com.payper.server.comment.dto;

import com.payper.server.comment.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponse {

    /**
     * Post에 달린 Comment Item
     */
    public record CommentItem(
            Long id,
            String userName,
            Long parentCommentId,
            String content,
            LocalDateTime createdAt,
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
    public record MyCommentList(
            List<CommentResponse.MyCommentItem> comments,
            Long nextCursor,
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
    public record MyCommentItem(
            Long id,
            Long postId,
            String content,
            LocalDateTime createdAt,
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