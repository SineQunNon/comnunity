package vet.webboard.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 30)
    private String nickname;

    @Column(length = 500)
    private String profileImage;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Post> posts = new ArrayList<>();
//
//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Comment> comments = new ArrayList<>();
//
//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<PostLike> postLikes = new ArrayList<>();

    @Builder
    public Member(String username, String password, String nickname, String profileImage) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    //비즈니스 메서드
    public void updateNickname(String nickname) {
        this.nickname = nickname;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePassword(String password) {
        this.password = password;
        this.updatedAt = LocalDateTime.now();
    }


}
