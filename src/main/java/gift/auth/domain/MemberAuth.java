package gift.auth.domain;

import org.springframework.util.Assert;

public record MemberAuth(
    Long memberId,
    Email email,
    String password,
    String refreshToken
) {

  public MemberAuth {
    validatePassword(password);
  }

  public static MemberAuth of(String email, String password) {
    return new MemberAuth(null, Email.createEmail(email), password, null);
  }

  public static MemberAuth of(String email, String password, String refreshToken) {
    return new MemberAuth(null, Email.createEmail(email), password, refreshToken);
  }

  public static MemberAuth withId(Long memberId, String email, String password) {
    validateId(memberId);
    return new MemberAuth(memberId, Email.createEmail(email), password, null);
  }

  public static MemberAuth withId(Long memberId, String email, String password,
      String refreshToken) {
    validateId(memberId);
    return new MemberAuth(memberId, Email.createEmail(email), password, refreshToken);
  }

  private static void validateId(Long id) {
    if (id == null || id < 0) {
      throw new IllegalArgumentException("id값은 null이거나 음수일 수 없습니다.");
    }
  }

  private static void validatePassword(String password) {
    Assert.hasText(password, "비밀번호는 null이거나 빈 값일 수 없습니다.");
  }

}
