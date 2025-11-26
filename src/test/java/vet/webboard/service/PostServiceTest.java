package vet.webboard.service;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BeanArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import vet.webboard.domain.Member;
import vet.webboard.domain.Post;
import vet.webboard.domain.PostImage;
import vet.webboard.dto.request.PostCreateRequest;
import vet.webboard.dto.response.PostResponse;
import vet.webboard.repository.MemberRepository;
import vet.webboard.repository.PostImageRepository;
import vet.webboard.repository.PostRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock PostRepository postRepository;
    @Mock MemberRepository memberRepository;
    @Mock PostImageRepository postImageRepository;

    @InjectMocks
    PostService postService;

    private Member member;
    private PostImage postImage;

    @BeforeEach
    void setUp() {


    }

    @DisplayName("게시글 작성 성공 - 이미지 없음")
    @Test
    void success_create_post() {
        //given
        Long memberId = 1L;
        Long postId = 1L;
        PostCreateRequest request = new PostCreateRequest("테스트 제목", "테스트 내용", null);
        Member member = Member.builder()
                .username("dleck28")
                .password("qwer1324")
                .nickname("sinequanon")
                .build();
        ReflectionTestUtils.setField(member, "id", memberId);

        Post post = request.toEntity(member);
        ReflectionTestUtils.setField(post, "id", postId);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(postRepository.save(any(Post.class))).willReturn(post);

        //when
        PostResponse savedPost = postService.createPost(request, memberId);

        //then
        assertThat(savedPost.getId()).isEqualTo(post.getId());
        assertThat(savedPost.getTitle()).isEqualTo(post.getTitle());
        assertThat(savedPost.getContent()).isEqualTo(post.getContent());

        verify(memberRepository, times(1)).findById(memberId);
        verify(postRepository, times(1)).save(any(Post.class));
    }
}
