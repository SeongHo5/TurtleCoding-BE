package net.turtlecoding.damgo.account.service;


import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.turtlecoding.damgo.account.dto.AccountInfoResponseDto;
import net.turtlecoding.damgo.account.dto.SignInRequestDto;
import net.turtlecoding.damgo.account.dto.SignInResponseDto;
import net.turtlecoding.damgo.account.dto.SignUpRequestDto;
import net.turtlecoding.damgo.account.entity.Account;
import net.turtlecoding.damgo.account.enums.RestrictedUsername;
import net.turtlecoding.damgo.account.repository.AccountRepository;
import net.turtlecoding.damgo.common.exception.AuthException;
import net.turtlecoding.damgo.common.exception.DuplicatedException;
import net.turtlecoding.damgo.common.exception.NotFoundException;
import net.turtlecoding.damgo.common.exception.enums.ExceptionStatus;
import net.turtlecoding.damgo.common.jwt.JwtProvider;
import net.turtlecoding.damgo.common.jwt.dto.JwtReissueResponseDto;
import net.turtlecoding.damgo.common.jwt.dto.JwtRequestDto;
import net.turtlecoding.damgo.common.jwt.dto.JwtResponseDto;
import net.turtlecoding.damgo.common.utils.RedisUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static net.turtlecoding.damgo.common.jwt.JwtAuthFilter.BLACK_LIST_KEY_PREFIX;
import static net.turtlecoding.damgo.common.jwt.JwtProvider.*;
import static org.springframework.beans.propertyeditors.CustomBooleanEditor.VALUE_TRUE;


@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisUtil redisUtil;
//    private final CookieUtil cookieUtil;

    @Override
    @Transactional
    public void createAccount(final SignUpRequestDto signUpRequestDto) {
        String requestedEmail = signUpRequestDto.getEmail();
        String requestedUsername = signUpRequestDto.getName();

        checkUsernameIsRestricted(requestedUsername);
        checkEmailIsDuplicated(requestedEmail);

        String encodedPassword = passwordEncoder.encode(signUpRequestDto.getPassword());
        Account account = signUpRequestDto.toEntity(encodedPassword);

        accountRepository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<AccountInfoResponseDto> getAccountInfo(final AccountDetails accountDetails) {
        Account requestedAccount = accountDetails.getAccount();
        return ResponseEntity
                .ok()
                .body(
                        new AccountInfoResponseDto(requestedAccount)
                );
    }

    @Override
    @Transactional
    public void modifyAccount(final AccountDetails accountDetails) {
        // TODO 곧
    }

    @Override
    @Transactional
    public void deleteAccount(final AccountDetails accountDetails) {
        accountRepository.delete(accountDetails.getAccount());
    }

    @Override
    @Transactional
    public ResponseEntity<SignInResponseDto> signIn(
            final SignInRequestDto signInRequestDto,
            HttpServletResponse response
    ) {
        String requestedEmail = signInRequestDto.getEmail();
        String requestedPassword = signInRequestDto.getPassword();
        Account requestedAccount = findAccountByEmail(requestedEmail);

        checkPasswordIsCorrect(requestedPassword, requestedAccount);

        Authentication authentication = jwtProvider.createAuthentication(requestedEmail);
        final JwtResponseDto jwtResponseDto = jwtProvider.createJwtToken(authentication);

        redisUtil.setDataExpire(authentication.getName(), jwtResponseDto.getRefreshToken(),
                REFRESH_TOKEN_EXPIRE_TIME);
//        ResponseCookie accessTokenCookie = cookieUtil.createCookie(
//                AUTHORIZATION_HEADER,
//                jwtResponseDto.getAccessToken()
//        );
        return ResponseEntity
                .ok()
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + jwtResponseDto.getAccessToken())
//                .headers(headers -> headers.add(
//                        "Set-Cookie",
//                        accessTokenCookie.toString()
//                ))
                .body(
                        SignInResponseDto.of(
                                requestedAccount.getName(),
                                requestedAccount.getRole(),
                                jwtResponseDto.getAccessToken(),
                                jwtResponseDto.getRefreshToken()

                        )
                );
    }

    /**
     * 로그아웃 처리를 위해 사용자 인증 수단(토큰)을 검증하고, 무효화한다.
     */
    @Override
    @Transactional
    public void signOut(
            final JwtRequestDto jwtRequestDto) {
        jwtProvider.validateToken(jwtRequestDto.getAccessToken());


        Claims claims = jwtProvider.getInfoFromToken(jwtRequestDto.getAccessToken());
        String email = claims.getSubject();

        invalidateToken(email, jwtRequestDto.getAccessToken());
    }

    @Override
    @Transactional
    public ResponseEntity<JwtReissueResponseDto> reissue(final JwtRequestDto jwtRequestDto) {
        validateRefreshToken(jwtRequestDto);
        validateRefreshTokenOwnership(jwtRequestDto);

        String email = jwtProvider.getInfoFromToken(jwtRequestDto.getAccessToken()).getSubject();

        Authentication authentication = jwtProvider.createAuthentication(email);
        JwtResponseDto jwtResponseDto = jwtProvider.createJwtToken(authentication);

        redisUtil.setDataExpire(email, jwtResponseDto.getRefreshToken(), REFRESH_TOKEN_EXPIRE_TIME);
        return ResponseEntity
                .ok()
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + jwtResponseDto.getAccessToken())
                .body(
                        JwtReissueResponseDto.of(
                                jwtResponseDto.getAccessToken(),
                                jwtResponseDto.getRefreshToken()
                        )
                );
    }

    // ------- 여기부터 private methods -------

    private void checkPasswordIsCorrect(String requestedPassword, Account account) {
        if (!passwordEncoder.matches(requestedPassword, account.getPassword())) {
            throw new AuthException(ExceptionStatus.INVALID_ID_IR_PW);
        }
    }


    private Account findAccountByEmail(final String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ExceptionStatus.NOT_FOUND_ACCOUNT));
    }

    private void checkEmailIsDuplicated(String email) {
        if (accountRepository.existsByEmail(email)) {
            throw new DuplicatedException(ExceptionStatus.CONFLICT_ACCOUNT);
        }
    }

    private void checkUsernameIsRestricted(String username) {
        for (RestrictedUsername restrictedUsername : RestrictedUsername.values()) {
            if (username.contains(restrictedUsername.getName())) {
                throw new AuthException(ExceptionStatus.RESTRICTED_USERNAME);
            }
        }
    }

    /**
     * 토큰의 유효성을 검증한다.
     */
    private void validateRefreshToken(JwtRequestDto jwtRequestDto) {
        jwtProvider.validateToken(jwtRequestDto.getRefreshToken());
    }

    /**
     * 요청자와 토큰의 소유자 정보가 일치하는지 검증한다.
     */
    private void validateRefreshTokenOwnership(JwtRequestDto jwtRequestDto) {
        String email = jwtProvider.getInfoFromToken(jwtRequestDto.getAccessToken()).getSubject();
        String validRefreshToken = redisUtil.getData(email);
        if (!jwtRequestDto.getRefreshToken().equals(validRefreshToken)) {
            throw new AuthException(ExceptionStatus.EXPIRED_REFRESH_TOKEN);
        }
    }

    /**
     * REDIS에서 사용자의 정보를 삭제하고, 토큰을 BLACK_LIST에 추가해 토큰을 무효화한다.
     */
    private void invalidateToken(String email, String accessToken) {
        redisUtil.deleteData(email);
        redisUtil.setDataExpire(
                BLACK_LIST_KEY_PREFIX + accessToken,
                VALUE_TRUE, ACCESS_TOKEN_EXPIRE_TIME / 1000L);
    }
}
