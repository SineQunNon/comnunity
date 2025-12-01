package vet.webboard.dto.response;

import lombok.Builder;
import lombok.Getter;
import vet.webboard.domain.Post;
import vet.webboard.domain.PostImage;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostDetailResponse {
    private Long id;
    private String title;
    private String content;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private MemberResponse member;
    private List<PostImageResponse> images;
    private List<CommentResponse> comments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostDetailResponse from(Post post) {
        return PostDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .member(MemberResponse.from(post.getMember()))
                .images(post.getPostImages().stream()
                        .map(PostImageResponse::from)
                        .toList())
                .comments(post.getComments().stream()
                        .map(CommentResponse::from)
                        .toList())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
