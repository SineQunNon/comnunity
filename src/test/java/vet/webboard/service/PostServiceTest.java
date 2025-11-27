package vet.webboard.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    PostRepository postRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    PostImageRepository postImageRepository;

    @InjectMocks
    PostService postService;

    private Member member;

    @BeforeEach
    void setUp() {
        Long memberId = 1L;
        member = Member.builder()
                .username("dleck28")
                .password("qwer1324")
                .nickname("sinequanon")
                .build();
        ReflectionTestUtils.setField(member, "id", memberId);
    }

    @DisplayName("게시글 작성 성공 - 이미지 없음")
    @Test
    void success_create_post() {
        //given
        Long memberId = 1L;
        Long postId = 1L;
        PostCreateRequest request = new PostCreateRequest("테스트 제목", "테스트 내용", null);
        Post post = request.toEntity(member);
        ReflectionTestUtils.setField(post, "id", postId);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(postRepository.save(any(Post.class))).willReturn(post);

        //when
        PostResponse savedPost = postService.createPost(request, memberId);

        //then
        assertThat(savedPost.getId()).isEqualTo(post.getId());
        assertThat(savedPost.getTitle()).isEqualTo(post.getTitle());
        assertThat(savedPost.getContent()).isEqualTo(post.getContent());

        verify(memberRepository, times(1)).findById(memberId);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    @DisplayName("게시글 수정 성공 - 제목과 내용만 수정")
    void updatePostSuccessOnlyTitleAndContent() {
        //given
        Member member = Member.builder()
                .username("test@test.com")
                .nickname("테스터")
                .password("qwer1234")
                .build();
        ReflectionTestUtils.setField(member, "id", 1L);
        Post post = Post.builder()
                .member(member)
                .title("원본 제목")
                .content("원본 내용")
                .build();
        ReflectionTestUtils.setField(post, "id", 1L);
        List<PostImage> postImages = new ArrayList<>();
        PostImage image1 = PostImage.builder()
                .post(post)
                .imageUrl("http://example.com/image1.jpg")
                .build();
        PostImage image2 = PostImage.builder()
                .post(post)
                .imageUrl("http://example.com/image2.jpg")
                .build();
        ReflectionTestUtils.setField(image1, "id", 1L);
        ReflectionTestUtils.setField(image2, "id", 2L);

        postImages.add(image1);
        postImages.add(image2);
        post.getPostImages().addAll(postImages);

        PostUpdateRequest request = PostUpdateRequest.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        //when
        PostDetailResponse response = postService.updatePost(1L, request, 1L);

        //then
        assertThat(response.getTitle()).isEqualTo("수정된 제목");
        assertThat(response.getContent()).isEqualTo("수정된 내용");
        assertThat(response.getImages()).hasSize(2);

        verify(postRepository).findById(1L);
        verify(postImageRepository, never()).deleteById(any());
        verify(postImageRepository, never()).save(any());
    }

    @Test
    @DisplayName("게시글 수정 성공 - 이미지 추가")
    void updatePostSuccessAddImages() {
        //given
        Member member = Member.builder()
                .username("test@test.com")
                .nickname("테스터")
                .password("qwer1234")
                .build();
        ReflectionTestUtils.setField(member, "id", 1L);
        Post post = Post.builder()
                .member(member)
                .title("원본 제목")
                .content("원본 내용")
                .build();
        ReflectionTestUtils.setField(post, "id", 1L);
        List<PostImage> postImages = new ArrayList<>();
        PostImage image1 = PostImage.builder()
                .post(post)
                .imageUrl("http://example.com/image1.jpg")
                .build();
        PostImage image2 = PostImage.builder()
                .post(post)
                .imageUrl("http://example.com/image2.jpg")
                .build();
        ReflectionTestUtils.setField(image1, "id", 1L);
        ReflectionTestUtils.setField(image2, "id", 2L);

        postImages.add(image1);
        postImages.add(image2);
        post.getPostImages().addAll(postImages);

        List<String> newImageUrls = List.of(
                "http://example.com/new1.jpg",
                "http://example.com/new2.jpg"
        );

        PostUpdateRequest request = PostUpdateRequest.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .imageUrls(newImageUrls)
                .build();

        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        //when
        PostDetailResponse response = postService.updatePost(1L, request, 1L);

        //then
        assertThat(response.getTitle()).isEqualTo("수정된 제목");
        assertThat(response.getContent()).isEqualTo("수정된 내용");
        assertThat(post.getPostImages()).hasSize(4);
        assertThat(response.getImages()).hasSize(4);

        verify(postRepository).findById(1L);
    }

    @Test
    @DisplayName("게시글 수정 성공 - 이미지 삭제 및 추가")
    void updatePostSuccessDeleteAndAddImages() {
        //given
        Member member = Member.builder()
                .username("test@test.com")
                .nickname("테스터")
                .password("qwer1234")
                .build();
        ReflectionTestUtils.setField(member, "id", 1L);
        Post post = Post.builder()
                .member(member)
                .title("원본 제목")
                .content("원본 내용")
                .build();
        ReflectionTestUtils.setField(post, "id", 1L);
        List<PostImage> postImages = new ArrayList<>();
        PostImage image1 = PostImage.builder()
                .post(post)
                .imageUrl("http://example.com/image1.jpg")
                .build();
        PostImage image2 = PostImage.builder()
                .post(post)
                .imageUrl("http://example.com/image2.jpg")
                .build();
        ReflectionTestUtils.setField(image1, "id", 1L);
        ReflectionTestUtils.setField(image2, "id", 2L);

        postImages.add(image1);
        postImages.add(image2);
        post.getPostImages().addAll(postImages);

        List<Long> deleteImageIds = List.of(1L);
        List<String> newImageUrls = List.of(
                "http://example.com/new1.jpg",
                "http://example.com/new2.jpg"
        );

        PostUpdateRequest request = PostUpdateRequest.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .deleteImageIds(deleteImageIds)
                .imageUrls(newImageUrls)
                .build();

        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        //when
        PostDetailResponse response = postService.updatePost(1L, request, 1L);

        //then
        assertThat(response.getTitle()).isEqualTo("수정된 제목");
        assertThat(response.getContent()).isEqualTo("수정된 내용");
        assertThat(post.getPostImages()).hasSize(3);
        assertThat(response.getImages()).hasSize(3);

        verify(postRepository).findById(1L);
    }
}
