package vet.webboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vet.webboard.domain.Member;
import vet.webboard.domain.Post;
import vet.webboard.domain.PostImage;
import vet.webboard.dto.request.PostCreateRequest;
import vet.webboard.dto.request.PostUpdateRequest;
import vet.webboard.dto.response.PostDetailResponse;
import vet.webboard.dto.response.PostResponse;
import vet.webboard.repository.MemberRepository;
import vet.webboard.repository.PostImageRepository;
import vet.webboard.repository.PostRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostImageRepository postImageRepository;

    @Transactional
    public PostResponse createPost(PostCreateRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 회원입니다."));
        Post savedPost = postRepository.save(request.toEntity(member));

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (String imageUrl : request.getImageUrls()) {
                PostImage postImage = PostImage.builder()
                        .post(savedPost)
                        .imageUrl(imageUrl)
                        .build();
                postImageRepository.save(postImage);
            }
        }
        return PostResponse.from(savedPost);
    }

    @Transactional
    public PostDetailResponse updatePost(Long postId, PostUpdateRequest request, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.isAuthor(memberId)) {
            throw new IllegalArgumentException("본인의 게시글만 수정할 수 있습니다.");
        }

        post.update(request.getTitle(), request.getContent());

        if (request.getDeleteImageIds() != null && !request.getDeleteImageIds().isEmpty()) {
            List<PostImage> imagesToRemove = post.getPostImages().stream()
                    .filter(image -> request.getDeleteImageIds().contains(image.getId()))
                    .toList();

            for (PostImage image : imagesToRemove) {
                post.removeImage(image);
            }
        }

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (String imageUrl : request.getImageUrls()) {
                PostImage postImage = PostImage.builder()
                        .post(post)
                        .imageUrl(imageUrl)
                        .build();
                post.addImage(postImage);
            }
        }
        return PostDetailResponse.from(post);
    }

    @Transactional
    public void deletePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.isAuthor(memberId)) {
            throw new IllegalArgumentException("게시글 작성자만 게시글을 삭제할 수 있습니다.");
        }

        postRepository.delete(post);
    }

    public List<PostResponse> findPosts() {
        return postRepository.findAll().stream()
                .map(PostResponse::from)
                .toList();
    }

    public PostDetailResponse findPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        return PostDetailResponse.from(post);
    }
}
