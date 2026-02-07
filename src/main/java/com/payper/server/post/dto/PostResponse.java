package com.payper.server.post.dto;

import com.payper.server.post.entity.Post;
import com.payper.server.post.entity.PostType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class PostResponse {

    /**
     * 게시글 단일 조회 DTO
     */
    @Schema(description = "게시글 상세 응답")
    public record PostDetail(
            @Schema(description = "게시글 ID", example = "1")
            Long id,
            @Schema(description = "작성자 이름", example = "홍길동")
            String authorName,
            @Schema(description = "가맹점명", example = "스타벅스 강남점")
            String merchantName,
            @Schema(description = "게시글 타입", example = "BENEFIT")
            PostType type,
            @Schema(description = "제목", example = "맛있는 맛집 추천합니다")
            String title,
            @Schema(description = "내용", example = "여기 정말 맛있어요!")
            String content,
            @Schema(description = "댓글 수", example = "5")
            long commentCount,
            @Schema(description = "조회 수", example = "100")
            long viewCount,
            @Schema(description = "좋아요 수", example = "10")
            long likeCount,
            @Schema(description = "작성일시")
            LocalDateTime createdAt,
            @Schema(description = "수정일시")
            LocalDateTime updatedAt
    ) {
        public static PostDetail from(Post post) {
            return new PostDetail(
                    post.getId(),
                    post.getAuthor().getName(),
                    post.getMerchant().getName(),
                    post.getType(),
                    post.getTitle(),
                    post.getContent(),
                    post.getCommentCount(),
                    post.getViewCount(),
                    post.getLikeCount(),
                    post.getCreatedAt(),
                    post.getUpdatedAt()
            );
        }
    }

    /**
     * 게시글 리스트 조회 DTO
     */
    @Schema(description = "게시글 목록 항목")
    public record PostList(
            @Schema(description = "게시글 ID", example = "1")
            Long id,
            @Schema(description = "작성자 이름", example = "홍길동")
            String authorName,
            @Schema(description = "가맹점명", example = "스타벅스 강남점")
            String merchantName,
            @Schema(description = "게시글 타입", example = "BENEFIT")
            PostType type,
            @Schema(description = "제목", example = "맛있는 맛집 추천합니다")
            String title,
            @Schema(description = "댓글 수", example = "5")
            long commentCount,
            @Schema(description = "조회 수", example = "100")
            long viewCount,
            @Schema(description = "좋아요 수", example = "10")
            long likeCount,
            @Schema(description = "작성일시")
            LocalDateTime createdAt

    ) {
        public static PostList from(Post post) {
            return new PostList(
                    post.getId(),
                    post.getAuthor().getName(),
                    post.getMerchant().getName(),
                    post.getType(),
                    post.getTitle(),
                    post.getCommentCount(),
                    post.getViewCount(),
                    post.getLikeCount(),
                    post.getCreatedAt()
            );
        }
    }
}
