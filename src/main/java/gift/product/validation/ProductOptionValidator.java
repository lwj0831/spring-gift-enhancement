package gift.product.validation;

import gift.product.exception.DuplicateProductOptionNameException;
import gift.product.repository.ProductOptionJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductOptionValidator {

    private final ProductOptionJpaRepository productOptionRepository;

    public ProductOptionValidator(ProductOptionJpaRepository productOptionRepository) {
        this.productOptionRepository = productOptionRepository;
    }

    public void validateOptionNameUniqueness(Long productId, String optionName) {
        if (productOptionRepository.existsByProductIdAndName(productId, optionName)) {
            throw new DuplicateProductOptionNameException(optionName);
        }
    }


}
