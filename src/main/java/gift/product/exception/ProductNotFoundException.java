package gift.product.exception;

import gift.global.exception.DomainNotFoundException;

public class ProductNotFoundException extends DomainNotFoundException {

    public ProductNotFoundException(Long id) {
        super(ProductErrorCode.PRODUCT_NOT_FOUND, id);
    }
}
