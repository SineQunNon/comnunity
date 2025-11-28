package vet.webboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vet.webboard.domain.Comment;
import vet.webboard.domain.Member;
import vet.webboard.domain.Post;
import vet.webboard.dto.request.CommentCreateRequest;
import vet.webboard.dto.request.CommentUpdateRequest;
import vet.webboard.dto.response.CommentResponse;
import vet.webboard.repository.CommentRepository;
import vet.webboard.repository.MemberRepository;
import vet.webboard.repository.PostRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public CommentResponse createComment(Long postId, CommentCreateRequest request, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return CommentResponse.from(commentRepository.save(request.toEntity(member, post)));
    }

    @Transactional
    public CommentResponse updateComment(CommentUpdateRequest request, Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        if (!comment.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
        }

        comment.update(request.getContent());

        return CommentResponse.from(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        if (!comment.isAuthor(memberId)) {
            throw new IllegalArgumentException("작성자 본인만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
    }
}
