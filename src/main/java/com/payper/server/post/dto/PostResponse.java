package com.payper.server.post.dto;

import com.payper.server.post.entity.Post;
import com.payper.server.post.entity.PostType;

import java.time.LocalDateTime;

public class PostResponse {

    /**
     * 게시글 단일 조회 DTO
     */
    public record postDetail(
            Long id,
            String authorName, /* 작성자 이름 */
            String merchantName, /* 가맹점명 */
            PostType type, /* 게시글 타입 */
            String title, /* 제목 */
            String content, /* 내용 */
            long commentCount, /* 댓글 수 */
            long viewCount, /* 조회 수 */
            long likeCount, /* 좋아요 수 */
            LocalDateTime createdAt, /* 글 작성 시간 */
            LocalDateTime updatedAt /* 글 수정 시간 */
    ) {
        public static postDetail from(Post post) {
            return new postDetail(
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
    public record postList(
            Long id,
            String authorName, /* 작성자 이름 */
            String merchantName, /* 가맹점명 */
            PostType type, /* 게시글 타입 */
            String title, /* 제목 */
            long commentCount, /* 댓글 수 */
            long viewCount, /* 조회 수 */
            long likeCount, /* 좋아요 수 */
            LocalDateTime createdAt /* 글 작성 시간 */

    ) {
        public static postList from(Post post) {
            return new postList(
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