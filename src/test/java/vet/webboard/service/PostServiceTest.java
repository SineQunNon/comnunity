package vet.webboard.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import vet.webboard.domain.Comment;
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
import static org.mockito.BDDMockito.willDoNothing;
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

    @Test
    @DisplayName("게시글 삭제 성공")
    void successDeletePost() {
        //given
        Long memberId = 1L;
        Long postId = 1L;
        PostCreateRequest request = new PostCreateRequest("테스트 제목", "테스트 내용", null);
        Post post = request.toEntity(member);
        ReflectionTestUtils.setField(post, "id", postId);

        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));
        willDoNothing().given(postRepository).delete(post);

        //when
        postService.deletePost(postId, memberId);

        //then
        verify(postRepository).findById(1L);
        verify(postRepository).delete(post);
    }

    @Test
    @DisplayName("게시글 삭제 성공 - cascade로 연관 데이터도 삭제")
    void successDeletePostWithRelatedData() {
        Long postId = 1L;
        PostCreateRequest request = new PostCreateRequest("테스트 제목", "테스트 내용", null);
        Post post = request.toEntity(member);
        ReflectionTestUtils.setField(post, "id", postId);

        Member otherMember = Member.builder()
                .username("dleck28")
                .password("qwer1324")
                .nickname("sinequanon")
                .build();
        ReflectionTestUtils.setField(otherMember, "id", 2L);

        PostImage image = PostImage.builder()
                .imageUrl("http://example.com/image.jpg")
                .build();
        post.addImage(image);

        // 댓글 추가
        Comment comment = Comment.builder()
                .member(otherMember)
                .content("댓글 내용")
                .build();
        ReflectionTestUtils.setField(comment, "post", post);

        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        willDoNothing().given(postRepository).delete(post);

        // when
        postService.deletePost(1L, 1L);

        // then
        verify(postRepository).findById(1L);
        verify(postRepository).delete(post);
    }

    @Test
    @DisplayName("게시글 목록 조회")
    void successToFindPosts() {
        //given
        Post post1 = Post.builder()
                .member(member)
                .title("테스트 제목1")
                .content("테스트 본문1")
                .build();
        Post post2 = Post.builder()
                .member(member)
                .title("테스트 제목2")
                .content("테스트 본문2")
                .build();
        ReflectionTestUtils.setField(post1, "id", 1L);
        ReflectionTestUtils.setField(post2, "id", 2L);
        member.addPost(post1);
        member.addPost(post2);
        given(postRepository.findAll()).willReturn(List.of(post1, post2));

        //when
        List<PostResponse> posts = postService.findPosts();

        //then
        assertThat(posts).hasSize(2);
        assertThat(posts.getFirst().getTitle()).isEqualTo("테스트 제목1");
        assertThat(posts.getFirst().getContent()).isEqualTo("테스트 본문1");
        assertThat(posts.getLast().getTitle()).isEqualTo("테스트 제목2");
        assertThat(posts.getLast().getContent()).isEqualTo("테스트 본문2");

        verify(postRepository).findAll();
    }

    @Test
    @DisplayName("게시글 상세 조회 성공")
    void successToFindPost() {
        //given
        Post post = Post.builder()
                .member(member)
                .title("테스트 제목1")
                .content("테스트 본문1")
                .build();
        ReflectionTestUtils.setField(post, "id", 1L);
        PostImage postImage1 = PostImage.builder()
                .imageUrl("http://example.com/test1")
                .post(post)
                .build();
        PostImage postImage2 = PostImage.builder()
                .imageUrl("http://example.com/test2")
                .post(post)
                .build();
        ReflectionTestUtils.setField(postImage1, "id", 1L);
        ReflectionTestUtils.setField(postImage2, "id", 2L);
        Comment comment = Comment.builder()
                .post(post)
                .member(member)
                .content("테스트 댓글1")
                .build();
        ReflectionTestUtils.setField(comment, "id", 1L);

        member.addPost(post);
        post.addImage(postImage1);
        post.addImage(postImage2);
        post.addComment(comment);
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

        //when
        PostDetailResponse response = postService.findPost(1L);

        //then
        assertThat(response.getTitle()).isEqualTo("테스트 제목1");
        assertThat(response.getContent()).isEqualTo("테스트 본문1");
        assertThat(response.getImages()).hasSize(2);
        assertThat(response.getComments()).hasSize(1);
        assertThat(response.getMember().getNickname()).isEqualTo("sinequanon");
        verify(postRepository, times(1)).findById(1L);
    }
}
