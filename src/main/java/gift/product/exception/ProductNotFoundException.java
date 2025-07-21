package gift.product.exception;

import gift.global.exception.BusinessException;

public class ProductNotFoundException extends BusinessException {

    private final Long notFoundId;

    public ProductNotFoundException(Long id) {
        super(ProductErrorCode.PRODUCT_NOT_FOUND);
        this.notFoundId = id;
    }

    public Long getNotFoundId() {
        return notFoundId;
    }
}
