package vet.webboard.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import vet.webboard.domain.Member;
import vet.webboard.domain.Post;
import vet.webboard.domain.PostLike;
import vet.webboard.dto.response.PostLikeResponse;
import vet.webboard.repository.MemberRepository;
import vet.webboard.repository.PostLikeRepository;
import vet.webboard.repository.PostRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PostLikeRepository postLikeRepository;

    @InjectMocks
    private PostLikeService postLikeService;

    private Member member;
    private Post post;

    @BeforeEach
    void setUp() {
        Long memberId = 1L;
        Long postId = 1L;
        member = Member.builder()
                .username("dleck28")
                .password("qwer1324")
                .nickname("sinequanon")
                .build();
        ReflectionTestUtils.setField(member, "id", memberId);

        post = Post.builder()
                .title("테스트 제목")
                .content("테스트 본문")
                .member(member)
                .build();
        ReflectionTestUtils.setField(post, "id", postId);
    }

    @Test
    @DisplayName("좋아요 추가 - 성공")
    void likePostSuccess() {
        //given
        Long memberId = 1L;
        Long postId = 1L;
        PostLike postLike = PostLike.builder()
                .member(member)
                .post(post)
                .build();
        ReflectionTestUtils.setField(postLike, "id", 1L);

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(postLikeRepository.existsByPostIdAndMemberId(postId, memberId)).willReturn(false);
        given(postLikeRepository.save(any(PostLike.class))).willReturn(postLike);

        //when
        PostLikeResponse response = postLikeService.likePost(postId, memberId);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getPostId()).isEqualTo(postId);
        assertThat(response.getLikeCount()).isEqualTo(1);
        assertThat(response.getIsLiked()).isTrue();
        assertThat(post.getLikeCount()).isEqualTo(1);  // 좋아요 수 증가 확인
        verify(postLikeRepository, times(1)).save(any(PostLike.class));
    }

    @Test
    @DisplayName("좋아요 삭제 성공")
    void unlikePostSuccess() {
        //given
        Long postId = 1L;
        PostLike postLike = PostLike.builder()
                .member(member)
                .post(post)
                .build();
        ReflectionTestUtils.setField(postLike, "id", 1L);

        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));
        given(postLikeRepository.findByPostIdAndMemberId(anyLong(), anyLong())).willReturn(Optional.of(postLike));
        willDoNothing().given(postLikeRepository).delete(postLike);

        //when
        postLikeService.unlikePost(postId, 1L);

        //then
        verify(postRepository).findById(1L);
        verify(postLikeRepository).findByPostIdAndMemberId(1L, 1L);
        verify(postLikeRepository).delete(postLike);
    }
}
