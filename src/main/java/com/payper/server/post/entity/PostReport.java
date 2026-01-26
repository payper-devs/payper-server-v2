package com.payper.server.post.entity;

import com.payper.server.global.entity.BaseTimeEntity;
import com.payper.server.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "post_report",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_post_report_post_reporter",
                columnNames = {"post_id", "reporter_id"}
        )
)
public class PostReport extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 신고 대상 게시글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /**
     * 신고자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    /**
     * 신고 사유
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportReason reason;

    /**
     * 상세 사유
     */
    private String description;
}