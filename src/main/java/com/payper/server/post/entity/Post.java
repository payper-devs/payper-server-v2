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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)

public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 작성자 TODO: 탈퇴한 유저라면 게시글을 보여줄 때 탈퇴한 유저라고 표시해야 함
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User author;

    /**
     * 대상 가맹점
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Merchant merchant;

    /**
     * 게시글 타입 (BENEFIT, PROMOTION, QUESTION, ETC)
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
     * 내용
     */
    @Lob
    @Column(nullable = false)
    private String content;

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
     * 게시글 삭제
     * soft delete
     */
    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}