package gift.wishlist.exception;

import gift.global.exception.BusinessException;

public class WishItemNotFoundException extends BusinessException {

  public WishItemNotFoundException() {
    super(WishItemErrorCode.WISH_ITEM_NOT_FOUND);
  }
}
