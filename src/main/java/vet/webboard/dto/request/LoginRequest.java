package vet.webboard.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginRequest {
    @NotBlank(message = "아이디는 필수입니다.")
    private String uesrname;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
