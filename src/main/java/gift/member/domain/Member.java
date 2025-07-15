package gift.member.domain;

import org.springframework.util.Assert;

public record Member(
    Long id,
    String name
) {

  public Member {
    validateName(name);
  }

  public static Member of(String name) {
    return new Member(null, name);
  }

  public static Member withId(Long id, String name) {
    validateId(id);
    return new Member(id, name);
  }

  private static void validateId(Long id) {
    if (id == null || id < 0) {
      throw new IllegalArgumentException("id값은 null이거나 음수일 수 없습니다.");
    }
  }

  private static void validateName(String name) {
    Assert.hasText(name, "name은 null이거나 빈 값일 수 없습니다.");
  }
}
