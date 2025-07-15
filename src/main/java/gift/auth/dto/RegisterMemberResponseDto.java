package gift.auth.dto;

import gift.auth.domain.TokenResponse;

public record RegisterMemberResponseDto(
    String tokenType,
    String accessToken,
    long expiresInSeconds,
    String refreshToken,
    long refreshTokenExpiresInSeconds,
    Long memberId
) {

  public static RegisterMemberResponseDto from(TokenResponse res, Long memberId) {
    return new RegisterMemberResponseDto(res.tokenType(), res.accessToken(), res.expiresIn(),
        res.refreshToken(), res.refreshTokenExpiresIn(), memberId);
  }

}
