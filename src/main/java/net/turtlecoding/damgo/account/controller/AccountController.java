package net.turtlecoding.damgo.account.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import net.turtlecoding.damgo.account.dto.AccountInfoResponseDto;
import net.turtlecoding.damgo.account.dto.SignInRequestDto;
import net.turtlecoding.damgo.account.dto.SignInResponseDto;
import net.turtlecoding.damgo.account.dto.SignUpRequestDto;
import net.turtlecoding.damgo.account.service.AccountDetails;
import net.turtlecoding.damgo.account.service.AccountServiceImpl;
import net.turtlecoding.damgo.common.jwt.dto.JwtReissueResponseDto;
import net.turtlecoding.damgo.common.jwt.dto.JwtRequestDto;
import net.turtlecoding.damgo.common.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AccountController {

    private final AccountServiceImpl accountServiceImpl;
    private final EmailService emailService;

    /**
     * 회원가입
     */
    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(final @Valid @RequestBody SignUpRequestDto signUpRequestDto) {
        accountServiceImpl.createAccount(signUpRequestDto);
    }

    /**
     * 로그인
     */
    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponseDto> signIn(
            final @Valid @RequestBody SignInRequestDto signInRequestDto,
            HttpServletResponse response
    ) {
        return accountServiceImpl.signIn(signInRequestDto, response);
    }

    /**
     * 로그아웃
     */
    @DeleteMapping("/sign-out")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_SELLER') or hasRole('ROLE_ADMIN')")
    public void signOut(
            final @RequestBody JwtRequestDto jwtRequestDto
    ) {
        accountServiceImpl.signOut(jwtRequestDto);
    }

    /**
     * 내 정보 조회
     */
    @GetMapping("/my-info")
    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_SELLER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<AccountInfoResponseDto> getAccountInfo(
            final @AuthenticationPrincipal AccountDetails accountDetails
    ) {
        return accountServiceImpl.getAccountInfo(accountDetails);
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/drop-out")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_SELLER')")
    public void dropOut(
            final @AuthenticationPrincipal AccountDetails accountDetails
    ) {
        accountServiceImpl.deleteAccount(accountDetails);
    }

    /**
     * 토큰 재발급
     */
    @PostMapping("/re-issue")
    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_SELLER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<JwtReissueResponseDto> reissue(
            final @RequestBody JwtRequestDto jwtRequestDto
    ) {
        return accountServiceImpl.reissue(jwtRequestDto);
    }

    /**
     * 본인 인증 이메일 발송
     *
     * @param email 메일 보낼 주소
     */
    @PostMapping("/send-email")
    @ResponseStatus(HttpStatus.OK)
    public void sendEmail(
            final @Email @RequestParam String email
    ) {
        emailService.sendVerificationMail(email);
    }

}
