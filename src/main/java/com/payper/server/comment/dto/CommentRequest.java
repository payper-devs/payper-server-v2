package com.payper.server.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentRequest {

    /**
     * 댓글 작성 DTO
     */
    @Schema(description = "댓글 작성 요청")
    public record CreateComment(
            @Schema(description = "댓글 내용", example = "좋은 글이네요!")
            @NotBlank(message = "댓글을 적어주세요.")
            @Size(max = 21800, message = "댓글은 21,800자 이하여야 합니다.")
            String content,

            @Schema(description = "부모 댓글 ID (대댓글 작성 시)", example = "1", nullable = true)
            @Nullable
            Long parentCommentId
    ) {}

    /**
     * 댓글 수정 DTO
     */
    @Schema(description = "댓글 수정 요청")
    public record UpdateComment(
            @Schema(description = "수정할 댓글 내용", example = "수정된 댓글입니다")
            @NotBlank(message = "댓글을 적어주세요.")
            @Size(max = 21800, message = "댓글은 21,800자 이하여야 합니다.")
            String content
    ) {}
}
