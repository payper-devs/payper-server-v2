package com.payper.server.post.repository;

import com.payper.server.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    Optional<Post> findByIdAndIsDeletedFalse(Long id);

    boolean existsByIdAndIsDeletedFalse(Long id);

    @Modifying
    @Query("""
        update Post p
        set p.commentCount = case
            when p.commentCount < :count then 0
            else p.commentCount - :count
        end
        where p.id = :postId
    """)
    void decreaseCommentCount(@Param("postId") Long postId, @Param("count") long count);
}