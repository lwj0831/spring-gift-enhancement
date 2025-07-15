package gift.auth.service;

import gift.auth.domain.JwtUtils;
import gift.auth.domain.TokenResponse;
import gift.auth.repository.MemberAuthRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TokenService {

  private final JwtUtils jwtUtils;
  private final MemberAuthRepository memberAuthRepository;
  private static final String AUTHORIZATION = "Authorization";
  private static final String TOKEN_TYPE_BEARER = "bearer";
  private static final String BEARER_PREFIX = "Bearer ";

  public TokenService(JwtUtils jwtUtils, MemberAuthRepository memberAuthRepository) {
    this.jwtUtils = jwtUtils;
    this.memberAuthRepository = memberAuthRepository;
  }

  public TokenResponse generateBearerTokenResponse(Long memberId, String email) {
    String accessToken = jwtUtils.createToken(memberId, email, List.of());
    String refreshToken = jwtUtils.createRefreshToken(memberId);

    memberAuthRepository.updateRefreshToken(memberId, refreshToken);

    long accessTokenExpiresIn = jwtUtils.getAccessTokenExpirationTime();
    long refreshTokenExpiresIn = jwtUtils.getRefreshTokenExpirationTime();

    return new TokenResponse(TOKEN_TYPE_BEARER, accessToken, accessTokenExpiresIn, refreshToken,
        refreshTokenExpiresIn);
  }

  public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION);
    if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(7);
    }
    return null;
  }

  public boolean isValidToken(String token) {
    return jwtUtils.validateToken(token);
  }

  public String getEmail(String token) {
    return jwtUtils.getEmail(token);
  }

  public Long getUserId(String token) {
    return jwtUtils.getUserId(token);
  }
}
