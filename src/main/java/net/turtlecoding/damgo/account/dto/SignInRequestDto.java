package net.turtlecoding.damgo.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class SignInRequestDto {

    @Email(message = "유효한 이메일 주소를 입력하세요.")
    private final @NotNull String email;

    @Size(min = 8, max = 20, message = "비밀번호는 8 ~ 20자 사이여야 합니다.")
    private final @NotNull String password;

}
