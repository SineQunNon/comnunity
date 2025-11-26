package vet.webboard.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import vet.webboard.domain.Comment;
import vet.webboard.domain.Member;
import vet.webboard.domain.Post;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentCreateRequest {
    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(max = 500, message = "댓글은 500자 이하여야 합니다.")
    private String content;

    public Comment toEntity(Member member, Post post) {
        return Comment.builder()
                .member(member)
                .post(post)
                .content(content)
                .build();
    }
}
