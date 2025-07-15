package gift.auth.domain;

public record TokenResponse(
    String tokenType,
    String accessToken,
    long expiresIn,
    String refreshToken,
    long refreshTokenExpiresIn
) {

}
