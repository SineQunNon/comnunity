package vet.webboard.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vet.webboard.domain.Member;
import vet.webboard.dto.request.LoginRequest;
import vet.webboard.dto.request.SignupRequest;
import vet.webboard.dto.response.LoginResponse;
import vet.webboard.dto.response.MemberResponse;
import vet.webboard.repository.MemberRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() {
        //given
        SignupRequest signupRequest = new SignupRequest("dleck288", "qwer1234", "qwer1234", "sinequanon", null);
        when(memberRepository.save(any())).thenReturn(signupRequest.toEntity());

        //when
        MemberResponse memberResponse = memberService.signup(signupRequest);

        //then
        assertThat(memberResponse.getUsername()).isEqualTo("dleck288");
        assertThat(memberResponse.getNickname()).isEqualTo("sinequanon");
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 아이디 중복")
    void duplicated_id_when_signup() {
        //given
        SignupRequest signupRequest = new SignupRequest("dleck288", "qwer1234", "qwer1234", "sinequanon", null);
        given(memberRepository.existsByUsername("dleck288")).willReturn(true);

        //when&then
        assertThatThrownBy(() -> memberService.signup(signupRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 사용 중인 아이디입니다.");
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 닉네임 중복")
    void duplicated_nickname_when_signup() {
        //given
        SignupRequest signupRequest = new SignupRequest("dleck288", "qwer1234", "qwer1234", "sinequanon", null);
        given(memberRepository.existsByNickname("sinequanon")).willReturn(true);

        //when&then
        assertThatThrownBy(() -> memberService.signup(signupRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 사용 중인 닉네임입니다.");
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 비밀번호 불일치")
    void mismatch_password_when_signup() {
        //given
        SignupRequest signupRequest = new SignupRequest("dleck288",
                "qwer1234", "123qwer1234",
                "sinequanon", null);
        given(memberRepository.existsByUsername("dleck288")).willReturn(false);
        given(memberRepository.existsByNickname("sinequanon")).willReturn(false);

        //when&then
        assertThatThrownBy(() -> memberService.signup(signupRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        //given
        LoginRequest loginRequest = new LoginRequest("dleck28", "qwer1234");
        Member member = new Member("dleck28", "qwer1234", "sinequanon", null);
        given(memberRepository.findByUsername(any())).willReturn(Optional.of(member));

        //when
        LoginResponse loginResponse = memberService.login(loginRequest);

        //then
        assertThat(loginResponse.getMember().getUsername()).isEqualTo("dleck28");
        verify(memberRepository).findByUsername("dleck28");
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 아이디")
    void wrong_id_when_login() {
        LoginRequest loginRequest = new LoginRequest("wrongId", "qwer1234");
        given(memberRepository.findByUsername(any())).willReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> memberService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디 또는 비밀번호가 일치하지 않습니다.");
        verify(memberRepository).findByUsername("wrongId");
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void wrong_password_when_login() {
        LoginRequest loginRequest = new LoginRequest("dleck28", "qwer1234");
        Member member = new Member("dleck28", "wrongpassword", "sinequanon", null);
        given(memberRepository.findByUsername(any())).willReturn(Optional.of(member));

        //when
        assertThatThrownBy(() -> memberService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디 또는 비밀번호가 일치하지 않습니다.");
        verify(memberRepository).findByUsername("dleck28");
    }
}
