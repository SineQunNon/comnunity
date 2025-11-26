package vet.webboard.dto.response;

import lombok.Builder;
import lombok.Getter;
import vet.webboard.domain.Member;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberResponse {
    private Long id;
    private String username;
    private String nickname;
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
