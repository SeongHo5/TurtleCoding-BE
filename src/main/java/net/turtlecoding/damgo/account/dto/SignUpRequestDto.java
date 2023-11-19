package net.turtlecoding.damgo.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import net.turtlecoding.damgo.account.entity.Account;
import net.turtlecoding.damgo.account.enums.Gender;
import net.turtlecoding.damgo.account.enums.UserRole;

import java.time.LocalDate;

@Value
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class SignUpRequestDto {

    @Pattern(regexp = "^[가-힣]{2,5}$", message = "이름은 한글만 가능합니다.")
    @NotNull String name;

    @Email(message = "유효한 이메일 주소를 입력하세요.")
    @NotNull String email;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()])[A-Za-z\\d!@#$%^&*()]{8,20}$",
            message = "비밀번호는 8자에서 20자 사이이며, 특수문자를 포함해야 합니다.")
    @NotNull String password;

    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "휴대폰 번호 형식만 가능합니다.")
    @NotNull String contact;


    public Account toEntity(String encodedPassword) {
        return Account.builder()
                .name(this.getName())
                .email(this.getEmail())
                .password(encodedPassword)
                .contact(this.getContact())
                .role(UserRole.ROLE_CUSTOMER)
                .build();
    }

    private Gender toGenderEnum(String gender) {
        return Gender.valueOf(gender.toUpperCase());
    }

}
