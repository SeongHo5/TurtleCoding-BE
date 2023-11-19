package net.turtlecoding.damgo.account.service;

import jakarta.servlet.http.HttpServletResponse;
import net.turtlecoding.damgo.account.dto.AccountInfoResponseDto;
import net.turtlecoding.damgo.account.dto.SignInRequestDto;
import net.turtlecoding.damgo.account.dto.SignInResponseDto;
import net.turtlecoding.damgo.account.dto.SignUpRequestDto;
import net.turtlecoding.damgo.common.jwt.dto.JwtReissueResponseDto;
import net.turtlecoding.damgo.common.jwt.dto.JwtRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


public interface AccountService {

    void createAccount(final SignUpRequestDto signUpRequestDto);

    void modifyAccount(final @AuthenticationPrincipal AccountDetails accountDetails);

    void deleteAccount(final @AuthenticationPrincipal AccountDetails accountDetails);

    ResponseEntity<AccountInfoResponseDto> getAccountInfo(final @AuthenticationPrincipal AccountDetails accountDetails);

    ResponseEntity<SignInResponseDto> signIn(
            final SignInRequestDto signInRequestDto,
            HttpServletResponse response
    );

    void signOut(final JwtRequestDto jwtRequestDto);

     ResponseEntity<JwtReissueResponseDto> reissue(
            final JwtRequestDto jwtRequestDto
    );



}
