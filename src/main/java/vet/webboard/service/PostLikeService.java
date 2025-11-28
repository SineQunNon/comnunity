package vet.webboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vet.webboard.domain.Member;
import vet.webboard.domain.Post;
import vet.webboard.domain.PostLike;
import vet.webboard.dto.response.PostLikeResponse;
import vet.webboard.repository.MemberRepository;
import vet.webboard.repository.PostLikeRepository;
import vet.webboard.repository.PostRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public PostLikeResponse likePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        if (postLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            return PostLikeResponse.builder()
                    .postId(postId)
                    .likeCount(post.getLikeCount())
                    .isLiked(true)
                    .build();
        }

        PostLike postLike = PostLike.builder()
                .post(post)
                .member(member)
                .build();

        postLikeRepository.save(postLike);
        post.increaseLikeCount();

        return PostLikeResponse.builder()
                .postId(postId)
                .likeCount(post.getLikeCount())
                .isLiked(true)
                .build();
    }

    @Transactional
    public PostLikeResponse unlikePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        PostLike postLike = postLikeRepository.findByPostIdAndMemberId(postId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("좋아요를 하지 않았습니다."));

        postLikeRepository.delete(postLike);
        post.decreaseLikeCount();

        return PostLikeResponse.builder()
                .postId(postId)
                .likeCount(post.getLikeCount())
                .isLiked(false)
                .build();
    }
}
