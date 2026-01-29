package com.payper.server.post.repository;

import com.payper.server.post.entity.Post;
import com.payper.server.post.entity.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByIdAndIsDeletedFalse(Long id);

    @Query(
        value = """
          select p from Post p
          join fetch p.author
          join fetch p.merchant
          where p.isDeleted = false
            and (:merchantId IS NULL or p.merchant.id = :merchantId)
            and (:type IS NULL or p.type = :type)
        """,
        countQuery = """
          select count(p) from Post p
          where p.isDeleted = false
            and (:merchantId IS NULL or p.merchant.id = :merchantId)
            and (:type IS NULL or p.type = :type)
        """
    )
    Page<Post> findActivePostsByCondition(
            @Param("merchantId") Long merchantId,
            @Param("type") PostType type,
            Pageable pageable
    );
}