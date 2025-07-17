package gift.wishlist.exception;

import gift.global.exception.DomainNotFoundException;

public class WishItemNotFoundException extends DomainNotFoundException {

    public WishItemNotFoundException(Long id) {
        super(WishItemErrorCode.WISH_ITEM_NOT_FOUND, id);
    }
}
