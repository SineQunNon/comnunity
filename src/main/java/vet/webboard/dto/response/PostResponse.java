package vet.webboard.dto.response;

import lombok.Builder;
import lombok.Getter;
import vet.webboard.domain.Post;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private MemberResponse member;
    private LocalDateTime createdAt;

    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .member(MemberResponse.from(post.getMember()))
                .createdAt(post.getCreatedAt())
                .build();
    }
}
