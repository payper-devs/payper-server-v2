package com.payper.server.post.entity;

import com.payper.server.card.entity.Card;
import com.payper.server.global.entity.BaseTimeEntity;
import com.payper.server.telecom.entity.Plan;
import com.payper.server.user.entity.User;
import com.payper.server.merchant.entity.Merchant;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Builder
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
     * 게시글 타입 (BENEFIT_CARD, BENEFIT_PLAN, PROMOTION, QUESTION, ETC)
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

    /* 카드 연결 */
    @ManyToOne(fetch = FetchType.LAZY)
    private Card card;

    /* 요금제 연결 */
    @ManyToOne(fetch = FetchType.LAZY)
    private Plan plan;

    /**
     * 시작 날짜
     */
    @Column(name = "start_date")
    private LocalDate startDate;

    /**
     * 종료 날짜
     */
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * 조회 수
     */
    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private long viewCount = 0;

    /**
     * 좋아요 수 TODO: PostLike 테이블 고려
     */
    @Builder.Default
    @Column(name = "like_count", nullable = false)
    private long likeCount = 0;

    /**
     * 신고 횟수
     */
    @Builder.Default
    @Column(name = "report_count", nullable = false)
    private long reportCount = 0;

    /**
     * 삭제 여부
     */
    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    // TODO: 게시물 검증 처리
}