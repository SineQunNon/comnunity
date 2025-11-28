package vet.webboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vet.webboard.domain.Member;
import vet.webboard.dto.request.LoginRequest;
import vet.webboard.dto.request.MemberPasswordUpdateRequest;
import vet.webboard.dto.request.MemberProfileUpdateRequest;
import vet.webboard.dto.request.SignupRequest;
import vet.webboard.dto.response.LoginResponse;
import vet.webboard.dto.response.MemberResponse;
import vet.webboard.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponse signup(SignupRequest request) {
        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (memberRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        if (!request.getPassword().equals(request.getPasswordConfirmed())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return MemberResponse.from(memberRepository.save(request.toEntity()));
    }

    public LoginResponse login(LoginRequest requestDto) {
        Member findMember = memberRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

        if (!requestDto.getPassword().equals(findMember.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        String accessToken = "token-test" + findMember.getPassword();

        return LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("tokenType")
                .expiresIn(7L)
                .member(MemberResponse.from(findMember))
                .build();
    }

    @Transactional
    public MemberResponse updateProfile(MemberProfileUpdateRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        member.updateNickname(request.getNickname());
        member.updateProfileImage(request.getProfileImage());
        return MemberResponse.from(member);
    }

    @Transactional
    public MemberResponse updatePassword(MemberPasswordUpdateRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (!request.getPassword().equals(request.getPasswordConfirmed())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        member.updatePassword(request.getPassword());
        return MemberResponse.from(member);
    }
}
