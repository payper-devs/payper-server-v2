package com.payper.server.comment.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentRequest {

    /**
     * 댓글 작성 DTO
     */
    public record CreateComment(
            @NotBlank(message = "댓글을 적어주세요.")
            @Size(max = 21800, message = "댓글은 21,800자 이하여야 합니다.")
            String content,

            @Nullable
            Long parentCommentId
    ) {}

    /**
     * 댓글 수정 DTO
     */
    public record UpdateComment(
            @NotBlank(message = "댓글을 적어주세요.")
            @Size(max = 21800, message = "댓글은 21,800자 이하여야 합니다.")
            String content
    ) {}
}