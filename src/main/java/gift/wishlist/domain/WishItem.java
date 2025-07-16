package gift.wishlist.domain;

import gift.member.domain.Member;
import gift.product.domain.Product;

public record WishItem(
    Long id,
    Member member,
    Product product
) {

  public WishItem {
    if (member == null) {
      throw new IllegalArgumentException("wishItem의 member는 null일 수 없습니다.");
    }
    if (product == null) {
      throw new IllegalArgumentException("wishItem의 product는 null일 수 없습니다.");
    }
  }

  public static WishItem of(Member member, Product product) {
    return new WishItem(null, member, product);
  }

  public static WishItem withId(Long id, Member member, Product product) {
    return new WishItem(id, member, product);
  }

}
