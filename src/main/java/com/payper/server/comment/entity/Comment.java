package com.payper.server.comment.entity;

import com.payper.server.global.entity.BaseTimeEntity;
import com.payper.server.post.entity.Post;
import com.payper.server.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comments")
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 게시글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /**
     * 댓글을 단 유저
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 부모 댓글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    /**
     * 댓글 내용 (64KB, 대략 21,800자)
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /**
     * 좋아요 수 TODO: CommentLike 테이블 고려
     */
    @Column(name = "like_count", nullable = false)
    private long likeCount;

    /**
     * 삭제 여부
     */
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    /**
     * 삭제 시간
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 댓글 삭제
     * soft delete
     */
    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}