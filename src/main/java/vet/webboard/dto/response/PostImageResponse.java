package vet.webboard.dto.response;

import lombok.Builder;
import lombok.Getter;
import vet.webboard.domain.PostImage;

@Getter
@Builder
public class PostImageResponse {
    private Long id;
    private String imageUrl;

    public static PostImageResponse from(PostImage postIMage) {
        return PostImageResponse.builder()
                .id(postIMage.getId())
                .imageUrl(postIMage.getImageUrl())
                .build();
    }
}
