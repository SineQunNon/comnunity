package vet.webboard.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vet.webboard.dto.request.LoginRequest;
import vet.webboard.dto.request.MemberProfileUpdateRequest;
import vet.webboard.dto.request.SignupRequest;
import vet.webboard.dto.response.LoginResponse;
import vet.webboard.dto.response.MemberResponse;
import vet.webboard.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<MemberResponse> signup(@RequestBody @Valid SignupRequest request) {
        MemberResponse response = memberService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse login = memberService.login(request);
        return ResponseEntity.ok(login);
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<MemberResponse> updateProfile(@PathVariable Long memberId,
                                                        @RequestBody @Valid MemberProfileUpdateRequest request) {
        MemberResponse memberResponse = memberService.updateProfile(request, memberId);
        return ResponseEntity.ok(memberResponse);
    }
}
