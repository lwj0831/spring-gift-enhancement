package gift.wishlist.domain;

public record WishItem(
    Long id,
    Long memberId,
    Long productId
) {

  public WishItem {
    validateId(memberId);
    validateId(productId);
  }

  public static WishItem of(Long memberId, Long productId) {
    return new WishItem(null, memberId, productId);
  }

  public static WishItem withId(Long id, Long memberId, Long productId) {
    return new WishItem(id, memberId, productId);
  }

  private void validateId(Long id) {
    if (id == null || id < 0) {
      throw new IllegalArgumentException("id는 null이거나 음수일 수 없습니다,");
    }
  }

}
