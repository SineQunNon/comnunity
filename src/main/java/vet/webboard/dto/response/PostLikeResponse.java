package vet.webboard.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostLikeResponse {
    private Long postId;
    private Integer likeCount;
    private Boolean isLiked;
}
