package com.payper.server.post.repository;

import com.payper.server.post.dto.PostResponse;
import com.payper.server.post.entity.Post;
import com.payper.server.post.entity.PostType;
import com.payper.server.post.entity.QPost;
import com.payper.server.merchant.entity.QMerchant;
import com.payper.server.user.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private static final QPost post = QPost.post;
    private static final QUser user = QUser.user;
    private static final QMerchant merchant = QMerchant.merchant;

    @Override
    public Page<PostResponse.PostList> findPostsWithDeferredJoin(Long merchantId, PostType type, Pageable pageable) {
        BooleanBuilder where = buildWhere(merchantId, type);
        OrderSpecifier<?>[] orderSpecifiers = buildOrderSpecifiers(pageable.getSort());

        // 1단계: ID만 조회 (인덱스 스캔, 지연 조인 효과)
        List<Long> ids = queryFactory
                .select(post.id)
                .from(post)
                .where(where)
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (ids.isEmpty()) {
            return Page.empty(pageable);
        }

        // 2단계: 확정된 ID에 대해서만 JOIN
        List<Post> posts = queryFactory
                .selectFrom(post)
                .join(post.author, user).fetchJoin()
                .join(post.merchant, merchant).fetchJoin()
                .where(post.id.in(ids))
                .orderBy(orderSpecifiers)
                .fetch();

        List<PostResponse.PostList> content = posts.stream()
                .map(PostResponse.PostList::from)
                .toList();

        // 3단계: 총 건수
        return PageableExecutionUtils.getPage(content, pageable, () ->
                queryFactory
                        .select(post.count())
                        .from(post)
                        .where(where)
                        .fetchOne()
        );
    }

    private BooleanBuilder buildWhere(Long merchantId, PostType type) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.isDeleted.eq(false));

        if (merchantId != null) {
            builder.and(post.merchant.id.eq(merchantId));
        }
        if (type != null) {
            builder.and(post.type.eq(type));
        }
        return builder;
    }

    private OrderSpecifier<?>[] buildOrderSpecifiers(Sort sort) {
        if (sort.isUnsorted()) {
            return new OrderSpecifier<?>[]{ post.createdAt.desc() };
        }

        return sort.stream()
                .map(order -> {
                    ComparableExpressionBase<?> path = switch (order.getProperty()) {
                        case "commentCount" -> post.commentCount;
                        case "likeCount" -> post.likeCount;
                        case "viewCount" -> post.viewCount;
                        default -> post.createdAt;
                    };
                    return order.isAscending() ? path.asc() : path.desc();
                })
                .toArray(OrderSpecifier<?>[]::new);
    }
}
