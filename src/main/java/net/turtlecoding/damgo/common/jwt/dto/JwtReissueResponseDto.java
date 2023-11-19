package net.turtlecoding.damgo.common.jwt.dto;


public record JwtReissueResponseDto(String accessToken, String refreshToken) {

    public static JwtReissueResponseDto of(String accessToken, String refreshToken) {
        return new JwtReissueResponseDto(accessToken, refreshToken);
    }
}
