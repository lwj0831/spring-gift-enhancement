package gift.wishlist.exception;

import gift.global.exception.BusinessException;

public class WishItemNotFoundException extends BusinessException {

    private final Long notFoundId;

    public WishItemNotFoundException(Long id) {
        super(WishItemErrorCode.WISH_ITEM_NOT_FOUND);
        this.notFoundId = id;
    }

    public Long getNotFoundId() {
        return notFoundId;
    }
}
