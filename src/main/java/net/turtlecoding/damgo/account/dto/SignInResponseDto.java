package net.turtlecoding.damgo.account.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.turtlecoding.damgo.account.enums.UserRole;


@Getter
@NoArgsConstructor(force = true)
public class SignInResponseDto {

    private final String userName;
    private final UserRole userRole;
    private final String accessToken;
    private final String refreshToken;

    public SignInResponseDto(String userName, UserRole userRole, String accessToken, String refreshToken) {
        this.userName = userName;
        this.userRole = userRole;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static SignInResponseDto of(
            String userName, UserRole userRole, String accessToken, String refreshToken
    ) {
        return new SignInResponseDto(userName, userRole, accessToken, refreshToken);
    }

}
