package vet.webboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vet.webboard.domain.Member;
import vet.webboard.dto.request.PostCreateRequest;
import vet.webboard.dto.response.PostResponse;
import vet.webboard.repository.MemberRepository;
import vet.webboard.repository.PostImageRepository;
import vet.webboard.repository.PostRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostImageRepository postImageRepository;

    @Transactional
    public PostResponse createPost(PostCreateRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 회원입니다."));
        return PostResponse.from(postRepository.save(request.toEntity(member)));
    }
}
