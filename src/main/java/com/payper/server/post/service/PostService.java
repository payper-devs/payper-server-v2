package com.payper.server.post.service;

import com.payper.server.global.exception.ApiException;
import com.payper.server.global.response.ErrorCode;
import com.payper.server.merchant.entity.Merchant;
import com.payper.server.merchant.repository.MerchantRepository;
import com.payper.server.post.dto.PostRequest;
import com.payper.server.post.dto.PostResponse;
import com.payper.server.post.entity.Post;
import com.payper.server.post.entity.PostType;
import com.payper.server.post.repository.PostRepository;
import com.payper.server.user.entity.User;
import com.payper.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final MerchantRepository merchantRepository;

    /**
     * 게시글 작성
     */
    @Transactional
    public Long createPost(Long userId, Long merchantId, PostRequest.CreatePost request) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        // 가맹점 조회
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ApiException(ErrorCode.MERCHANT_NOT_FOUND));

        // 게시글 생성
        Post post = Post.create(user, merchant, request.type(), request.title(), request.content());
        postRepository.save(post);

        log.info("게시글 생성 완료 - postId: {}, userId: {}, merchantId: {}", post.getId(), userId, merchantId);
        return post.getId();
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public void updatePost(Long userId, Long postId, PostRequest.UpdatePost request) {
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(ErrorCode.POST_NOT_FOUND));

        // 게시글 수정 권한 조회
        if(!userId.equals(post.getAuthor().getId())) {
            throw new ApiException(ErrorCode.NOT_POSTING_USER);
        }

        // 게시글 수정
        post.update(request.title(), request.content());

        log.info("게시글 수정 완료 - postId: {}", post.getId());
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deletePost(Long userId, Long postId) {
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(ErrorCode.POST_NOT_FOUND));

        // 게시글 삭제 권한 조회
        if(!userId.equals(post.getAuthor().getId())) {
            throw new ApiException(ErrorCode.NOT_POSTING_USER);
        }

        // 게시글 삭제
        post.delete();
        log.info("게시글 삭제 완료 - postId: {}", post.getId());

        // TODO 댓글 삭제 (대댓글도 삭제)
//        commentRepository.softDeleteByPostId(postId);
    }

    /**
     * 게시글 단일 조회
     */
    @Transactional(readOnly = true)
    public PostResponse.PostDetail getPostDetail(Long postId) {
        // 게시글 조회
        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new ApiException(ErrorCode.POST_NOT_FOUND));

        return PostResponse.PostDetail.from(post);
    }

    /**
     * 게시글 리스트 조회
     */
    @Transactional(readOnly = true)
    public Page<PostResponse.PostList> getPosts(Long merchantId, PostType type, Pageable pageable) {
        Page<Post> posts = postRepository.findActivePostsByCondition(merchantId, type, pageable);

        log.info("post 조회 완료 {}", posts);
        return posts.map(PostResponse.PostList::from);
    }
}