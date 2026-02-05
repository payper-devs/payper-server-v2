package com.payper.server.post.entity;

import com.payper.server.global.entity.BaseTimeEntity;
import com.payper.server.user.entity.User;
import com.payper.server.merchant.entity.Merchant;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "posts")
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 작성자 TODO: 탈퇴한 유저라면 게시글을 보여줄 때 탈퇴한 유저라고 표시해야 함
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /**
     * 대상 가맹점
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    /**
     * 게시글 타입 (BENEFIT, QUESTION, ETC)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostType type;

    /**
     * 제목
     */
    @Column(nullable = false)
    private String title;

    /**
     * 내용 (16MB, 대략 5,592,400자)
     */
    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    private String content;

    /**
     * 댓글 수
     */
    @Column(name = "comment_count", nullable = false)
    private long commentCount;

    /**
     * 조회 수
     */
    @Column(name = "view_count", nullable = false)
    private long viewCount;

    /**
     * 좋아요 수 TODO: PostLike 테이블 고려
     */
    @Column(name = "like_count", nullable = false)
    private long likeCount;

    /**
     * 신고 수 TODO: PostReport 테이블 고려
     */
    @Column(name = "report_count", nullable = false)
    private long reportCount;

    /**
     * 비활성 여부
     */
    @Column(name = "is_inactive", nullable = false)
    private boolean isInactive;

    /**
     * 비활성 시간
     */
    @Column(name = "inactive_at")
    private LocalDateTime inactiveAt;

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
     * 댓글 증가
     */
    public void increaseCommentCount() {
        this.commentCount++;
    }

    /**
     * 댓글 감소
     */
    public void decreaseCommentCount() {
        this.commentCount = Math.max(0, this.commentCount - 1);
    }

    /**
     * 게시글 삭제
     * soft delete
     */
    public void delete() {
        if (this.isDeleted) { // 멱등성 고려 처리
            return;
        }

        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.commentCount = 0;
    }

    /**
     * 게시글 비활성
     */
    public void inactivate() {
        this.isInactive = true;
//        this.inactiveAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        this.inactiveAt = LocalDateTime.now();
    }

    /**
     * 게시글 생성
     */
    public static Post create(User author, Merchant merchant, PostType type, String title, String content) {
        return Post.builder()
                .author(author)
                .merchant(merchant)
                .type(type)
                .title(title)
                .content(content)
                .build();
    }

    /**
     * 게시글 수정
     */
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    /**
     * 댓글을 달 수 있는 게시글인지 체크
     */
    public boolean isCommentable() {
        return !this.isDeleted && !this.isInactive;
    }
}