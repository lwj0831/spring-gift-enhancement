package gift.product.service;

import gift.global.common.dto.PageResponseDto;
import gift.product.domain.Product;
import gift.product.domain.ProductOption;
import gift.product.dto.CreateProductOptionDto;
import gift.product.dto.CreateProductOptionsRequestDto;
import gift.product.dto.GetProductOptionResponseDto;
import gift.product.exception.ProductOptionNotFoundException;
import gift.product.repository.ProductOptionJpaRepository;
import gift.product.validation.ProductOptionValidator;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductOptionService {

    private final ProductOptionJpaRepository productOptionRepository;
    private final ProductService productService;
    private final ProductOptionValidator productOptionValidator;

    public ProductOptionService(ProductOptionJpaRepository productOptionRepository,
        ProductService productService,
        ProductOptionValidator productOptionValidator) {
        this.productOptionRepository = productOptionRepository;
        this.productService = productService;
        this.productOptionValidator = productOptionValidator;
    }

    @Transactional
    public Long registerProductOption(Long productId, CreateProductOptionDto dto) {
        Product product = productService.findProductOrThrow(productId);

        productOptionValidator.validateOptionNameUniqueness(productId, dto.name());

        ProductOption newProductOption = ProductOption.of(dto.name(), dto.quantity(), product);
        return productOptionRepository.save(newProductOption).getId();
    }

    @Transactional
    public void registerProductOptionList(Long productId, CreateProductOptionsRequestDto dto) {
        Product product = productService.findProductOrThrow(productId);

        dto.optionRequestDtoList().forEach(
            opt -> productOptionValidator.validateOptionNameUniqueness(productId,
                opt.name())
        );

        List<ProductOption> options = dto.optionRequestDtoList().stream()
            .map(option -> ProductOption.of(option.name(), option.quantity(), product))
            .toList();

        productOptionRepository.saveAll(options);
    }

    @Transactional(readOnly = true)
    public PageResponseDto<GetProductOptionResponseDto> getProductOptions(Long productId,
        Pageable pageable) {
        productService.findProductOrThrow(productId);

        return PageResponseDto.from(productOptionRepository.findAllByProductId(productId, pageable)
            .map(GetProductOptionResponseDto::from));
    }

    @Transactional
    public void addProductOptionQuantity(Long optionId, int quantity) {
        ProductOption option = findProductOptionOrThrow(optionId);

        option.addQuantity(quantity);
    }

    @Transactional
    public void subtractProductOptionQuantity(Long optionId, int quantity) {
        ProductOption option = findProductOptionOrThrow(optionId);

        option.subtractQuantity(quantity);
    }

    @Transactional
    public void deleteProductOption(Long optionId) {
        ProductOption option = findProductOptionOrThrow(optionId);

        productOptionRepository.delete(option);
    }

    public ProductOption findProductOptionOrThrow(Long id) {
        return productOptionRepository.findById(id)
            .orElseThrow(() -> new ProductOptionNotFoundException(id));
    }


}
