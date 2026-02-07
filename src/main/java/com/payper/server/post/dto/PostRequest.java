package com.payper.server.post.dto;

import com.payper.server.post.entity.PostType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PostRequest {

    /**
     * 게시글 작성 DTO
     */
    @Schema(description = "게시글 작성 요청")
    public record CreatePost(
            @Schema(description = "게시글 타입 : BENEFIT|QUESTION|ETC ", example = "BENEFIT")
            @NotNull(message = "게시글의 타입을 선택해주세요.")
            PostType type,

            @Schema(description = "게시글 제목", example = "맛있는 맛집 추천합니다")
            @NotBlank(message = "제목을 적어주세요.")
            String title,

            @Schema(description = "게시글 내용", example = "여기 정말 맛있어요!")
            @NotBlank(message = "내용을 적어주세요.")
            @Size(max = 5500000, message = "내용은 500만자 이내로 적어주세요.")
            String content
    ) {}

    /**
     * 게시글 수정 DTO
     */
    @Schema(description = "게시글 수정 요청")
    public record UpdatePost(
            @Schema(description = "수정할 제목", example = "수정된 제목입니다")
            @NotBlank(message = "제목을 적어주세요.")
            String title,

            @Schema(description = "수정할 내용", example = "수정된 내용입니다")
            @NotBlank(message = "내용을 적어주세요.")
            @Size(max = 5500000, message = "내용은 500만자 이내로 적어주세요.")
            String content
    ) {}
}
