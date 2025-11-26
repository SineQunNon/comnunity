package vet.webboard.service;

import org.assertj.core.api.Assertions;
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
import vet.webboard.dto.request.CommentCreateRequest;
import vet.webboard.dto.response.CommentResponse;
import vet.webboard.repository.CommentRepository;
import vet.webboard.repository.MemberRepository;
import vet.webboard.repository.PostRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock MemberRepository memberRepository;
    @Mock PostRepository postRepository;
    @Mock CommentRepository commentRepository;

    @InjectMocks CommentService commentService;

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
    @DisplayName("댓글 작성 - 정상 등록")
    void createPostSuccess() {
        //given
        Long memberId = 1L;
        Long postId = 1L;
        CommentCreateRequest request = new CommentCreateRequest("테스트 댓글");
        Comment comment = request.toEntity(member, post);
        ReflectionTestUtils.setField(comment, "id", 1L);

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(commentRepository.save(any(Comment.class))).willReturn(comment);

        //when
        CommentResponse response = commentService.createComment(postId, request, memberId);
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo("테스트 댓글");
        assertThat(response.getMember().getId()).isEqualTo(1L);
        verify(commentRepository).save(any(Comment.class));
    }
}
