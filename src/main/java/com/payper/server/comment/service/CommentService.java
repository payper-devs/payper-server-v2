package com.payper.server.comment.service;

import com.payper.server.comment.dto.CommentRequest;
import com.payper.server.comment.dto.CommentResponse;
import com.payper.server.comment.entity.Comment;
import com.payper.server.comment.repository.CommentRepository;
import com.payper.server.global.exception.ApiException;
import com.payper.server.global.response.ErrorCode;
import com.payper.server.post.entity.Post;
import com.payper.server.post.repository.PostRepository;
import com.payper.server.user.entity.User;
import com.payper.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    /**
     * 댓글 작성
     */
    @Transactional
    public Long createComment(Long userId, Long postId, CommentRequest.CreateComment request) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(ErrorCode.POST_NOT_FOUND));

        // 삭제 또는 비활성화된 post에는 댓글 작성 불가
        if (!post.isCommentable()) {
            throw new ApiException(ErrorCode.POST_NOT_COMMENTABLE);
        }

        Comment parentComment = request.parentCommentId() != null
                ? getValidatedParentComment(request.parentCommentId(), postId)
                : null;

        // 댓글 생성
        Comment comment = Comment.create(post, user, parentComment, request.content());
        commentRepository.save(comment);

        post.increaseCommentCount();

        log.info("댓글 생성 완료 - commentId: {}, userId: {}, postId: {}", comment.getId(), userId, postId);
        return comment.getId();
    }

    // 요청 body로 받은 parent comment id 검증
    private Comment getValidatedParentComment(Long parentCommentId, Long postId) {
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND));

        if (!parentComment.getPost().getId().equals(postId)) {
            throw new ApiException(ErrorCode.INVALID_PARENT_COMMENT);
        }

        return parentComment;
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public void updateComment(Long userId, Long commentId, CommentRequest.UpdateComment request) {
        // 댓글 조회
        Comment comment = commentRepository.findByIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND));

        // 댓글 수정 권한 조회
        if(!comment.isAuthor(userId)) {
            throw new ApiException(ErrorCode.NOT_COMMENT_AUTHOR);
        }

        // 댓글 수정
        comment.update(request.content());

        log.info("댓글 수정 완료 - commentId: {}", comment.getId());
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        // 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND));

        // 댓글 삭제 권한 조회
        if(!comment.isAuthor(userId)) {
            throw new ApiException(ErrorCode.NOT_COMMENT_AUTHOR);
        }

        comment.delete();
    }

    /**
     * 내가 작성한 댓글 조회
     */
    @Transactional(readOnly = true)
    public CommentResponse.MyCommentList getMyComments(Long userId, Long cursorId, int size) {
        Pageable pageable = PageRequest.of(0, size);

        Slice<Comment> comments;

        if (cursorId == null) { // 첫 요청
            comments = commentRepository.findFirstMyCommentPage(userId, pageable);
        } else { // 첫 요청이 아닌 경우
            Comment lastComment = commentRepository.findById(cursorId)
                    .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND));

            comments = commentRepository.findNextMyCommentPage(userId, cursorId, lastComment.getCreatedAt(), pageable);
        }

        Long nextCursor = comments.hasNext() ? comments.getContent().get(comments.getContent().size()-1).getId() : null;
        return CommentResponse.MyCommentList.from(comments.getContent(), nextCursor, comments.hasNext());
    }

    /**
     * 게시글 댓글 조회
     */
    @Transactional(readOnly = true)
    public CommentResponse.CommentList getPostComments(Long postId, Long cursorId, int size) {

        // 게시글 존재 및 삭제 여부 확인
        if(!postRepository.existsByIdAndIsDeletedFalse(postId)) {
            throw new ApiException(ErrorCode.POST_NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(0, size);

        Slice<Comment> comments;

        if (cursorId == null) {
            comments = commentRepository.findParent(postId, pageable);
        } else {
            Comment lastComment = commentRepository.findById(cursorId)
                    .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND));

            comments = commentRepository.findParentNext(postId, cursorId, lastComment.getCreatedAt(), pageable);
        }

        Long nextCursor = comments.hasNext() ? comments.getContent().get(comments.getContent().size()-1).getId() : null;
        return CommentResponse.CommentList.from(comments.getContent(), nextCursor, comments.hasNext());
    }

    /**
     * 자식 댓글 조회
     */
    @Transactional(readOnly = true)
    public CommentResponse.CommentList getReplies(Long parentId, Long cursorId, int size) {

        // 부모 댓글 존재 여부 확인
        if(!commentRepository.existsById(parentId)) {
            throw new ApiException(ErrorCode.COMMENT_NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(0, size);

        Slice<Comment> comments;

        if (cursorId == null) {
            comments = commentRepository.findReply(parentId, pageable);
        } else {
            Comment lastComment = commentRepository.findById(cursorId)
                    .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND));

            comments = commentRepository.findReplyNext(parentId, cursorId, lastComment.getCreatedAt(), pageable);
        }

        Long nextCursor = comments.hasNext() ? comments.getContent().get(comments.getContent().size()-1).getId() : null;
        return CommentResponse.CommentList.from(comments.getContent(), nextCursor, comments.hasNext());
    }

    /**
     * 게시글 삭제 시 댓글 삭제(soft delete)
     */
    @Transactional(propagation = REQUIRES_NEW)
    public void softDeleteByPostId(Long postId) {
        long deletedCount = commentRepository.softDeleteByPostId(postId, LocalDateTime.now());
        if (deletedCount > 0) {
            postRepository.decreaseCommentCount(postId, deletedCount);
        }
    }
}
