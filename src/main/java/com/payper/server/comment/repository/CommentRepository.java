package com.payper.server.comment.repository;

import com.payper.server.comment.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndIsDeletedFalse(Long id);

    @Query("""
        select c from Comment c
        where c.user.id = :userId
          and c.isDeleted = false
        order by c.createdAt desc, c.id desc
    """)
    List<Comment> findFirstMyCommentPage(@Param("userId") Long userId, Pageable pageable);

    @Query("""
        select c from Comment c
        where c.user.id = :userId
          and c.isDeleted = false
          and (
                c.createdAt < :createdAt
                or (c.createdAt = :createdAt and c.id < :cursorId)
              )
        order by c.createdAt desc, c.id desc
    """)
    List<Comment> findNextMyCommentPage(
            @Param("userId") Long userId,
            @Param("cursorId") Long cursorId,
            @Param("createdAt") LocalDateTime createdAt,
            Pageable pageable
    );

    @Query("""
            select c from Comment c
            join fetch c.user
            where c.post.id = :postId
            and(
                c.isDeleted = false
                or exists (
                    select 1
                    from Comment child
                    where child.parentComment = c
                        and child.isDeleted = false
                )
            )
            ORDER BY COALESCE(c.parentComment.id, c.id), c.createdAt, c.id
    """)
    List<Comment> findVisibleCommentsByPostId(Long postId);

//    @Modifying
//    @Query("""
//        update Comment c
//        set c.isDeleted = true,
//            c.deletedAt = CURRENT_TIMESTAMP
//        where c.post.id = :postId
//          and c.isDeleted = false
//    """)
//    void softDeleteByPostId(Long postId);
}
