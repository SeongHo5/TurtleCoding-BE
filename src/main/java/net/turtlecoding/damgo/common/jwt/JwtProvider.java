package net.turtlecoding.damgo.common.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.turtlecoding.damgo.account.service.AccountDetailsServiceImpl;
import net.turtlecoding.damgo.common.exception.AuthException;
import net.turtlecoding.damgo.common.jwt.dto.JwtResponseDto;
import net.turtlecoding.damgo.common.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

import static net.turtlecoding.damgo.common.exception.enums.ExceptionStatus.*;


@Setter
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    private final RedisUtil redisUtil;
    private final AccountDetailsServiceImpl accountDetailsServiceImpl;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_KEY = "auth";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 15; // 15분
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 7 * 2; // 2주

    @Value("${spring.jwt.secretKey}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // secretKey를 Base64로 인코딩
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    /**
     * 토큰을 검증하기 위해 Request Header에서 "Bearer "를 제거한다.
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public JwtResponseDto createJwtToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        long now = (new Date()).getTime();

        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORIZATION_KEY, authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, signatureAlgorithm)
                .compact();

        Date refreshTokenExpiresIn = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);
        String refreshToken = Jwts.builder()
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, signatureAlgorithm)
                .compact();

        return JwtResponseDto.builder()
                .grantType(BEARER_PREFIX)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }

    public void validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

            if (redisUtil.hasKey("JWT:BLACK_LIST:" + token)) {
                throw new AuthException(BLACKLISTED_TOKEN);
            }
        } catch (SecurityException | MalformedJwtException e) {
            log.info("유효하지 않은 JWT 서명");
            throw new AuthException(INVALID_AUTH_ERROR);
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰");
            throw new AuthException(INVALID_AUTH_ERROR);
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
            throw new AuthException(INVALID_AUTH_ERROR);
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰");
            throw new AuthException(EXPIRED_TOKEN);
        }
    }

    public Claims getInfoFromToken(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public Authentication createAuthentication(String email) {
        UserDetails userDetails = accountDetailsServiceImpl.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public Long getExpiration(String token) {
        long expiration = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration().getTime();
        LocalDateTime origin = LocalDateTime.of(1970, 1, 1, 0, 0);
        long now = origin.until(LocalDateTime.now(), ChronoUnit.MILLIS);
        return now - expiration;
    }
}
