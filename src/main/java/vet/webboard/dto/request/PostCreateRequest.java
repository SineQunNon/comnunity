package vet.webboard.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vet.webboard.domain.Member;
import vet.webboard.domain.Post;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostCreateRequest {
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
    private String title;

    @NotBlank(message = "본문은 필수입니다.")
    private String content;

    private List<String> imageUrls;

    public Post toEntity(Member member) {
        return Post.builder()
                .member(member)
                .title(title)
                .content(content)
                .build();
    }
}
