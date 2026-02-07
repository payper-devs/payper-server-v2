package com.payper.server.comment.entity;

import com.payper.server.global.entity.BaseTimeEntity;
import com.payper.server.post.entity.Post;
import com.payper.server.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
        if (this.isDeleted) { // 멱등성 고려
            return;
        }

        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.post.decreaseCommentCount();
    }

    /**
     * 댓글 생성
     */
    public static Comment create(Post post, User user, Comment parentComment, String content) {
        return Comment.builder()
                .post(post)
                .user(user)
                .parentComment(parentComment)
                .content(content)
                .likeCount(0)
                .isDeleted(false)
                .build();
    }

    /**
     * 댓글 수정
     */
    public void update(String content) {
        this.content = content;
    }
}