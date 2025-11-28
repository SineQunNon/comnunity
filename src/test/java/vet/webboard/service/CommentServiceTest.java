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
import vet.webboard.dto.request.CommentUpdateRequest;
import vet.webboard.dto.response.CommentResponse;
import vet.webboard.repository.CommentRepository;
import vet.webboard.repository.MemberRepository;
import vet.webboard.repository.PostRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    MemberRepository memberRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    CommentService commentService;

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

    @Test
    @DisplayName("댓글 수정 성공")
    void updateCommentSuccess() {
        //given
        Comment comment = Comment.builder()
                .member(member)
                .post(post)
                .content("댓글 테스트")
                .build();
        ReflectionTestUtils.setField(comment, "id", 1L);
        CommentUpdateRequest request = new CommentUpdateRequest("댓글 수정 테스트");
        given(commentRepository.findById(anyLong())).willReturn(Optional.of(comment));

        //when
        CommentResponse response = commentService.updateComment(request, 1L, 1L);

        //then
        assertThat(response.getContent()).isEqualTo("댓글 수정 테스트");
        verify(commentRepository).findById(1L);
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteCommentSuccess() {
        //given
        Comment comment = Comment.builder()
                .member(member)
                .post(post)
                .content("댓글 테스트")
                .build();
        ReflectionTestUtils.setField(comment, "id", 1L);

        given(commentRepository.findById(anyLong())).willReturn(Optional.of(comment));
        willDoNothing().given(commentRepository).delete(comment);

        //when
        commentService.deleteComment(1L, 1L);

        //then
        verify(commentRepository).findById(1L);
        verify(commentRepository).delete(comment);
    }
}
