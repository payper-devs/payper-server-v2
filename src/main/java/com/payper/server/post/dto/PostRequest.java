package com.payper.server.post.dto;

import com.payper.server.post.entity.PostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PostRequest {

    /**
     * 게시글 작성 DTO
     */
    public record CreatePost(
            @NotNull(message = "게시글의 타입을 선택해주세요.")
            PostType type,

            @NotBlank(message = "제목을 적어주세요.")
            String title,

            @NotBlank(message = "내용을 적어주세요.")
            @Size(max = 5500000, message = "내용은 500만자 이내로 적어주세요.")
            String content
    ) {}

    /**
     * 게시글 수정 DTO
     */
    public record UpdatePost(
            @NotBlank(message = "제목을 적어주세요.")
            String title,

            @NotBlank(message = "내용을 적어주세요.")
            @Size(max = 5500000, message = "내용은 500만자 이내로 적어주세요.")
            String content
    ) {}
}