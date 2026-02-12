package com.payper.server.post.repository;

import com.payper.server.post.dto.PostResponse;
import com.payper.server.post.entity.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {

    Page<PostResponse.PostList> findPostsWithDeferredJoin(Long merchantId, PostType type, Pageable pageable);
}
