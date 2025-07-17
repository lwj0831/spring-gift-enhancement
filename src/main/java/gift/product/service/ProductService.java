package gift.product.service;

import gift.global.common.dto.PageRequestDto;
import gift.global.common.dto.PageResponseDto;
import gift.product.domain.Product;
import gift.product.dto.CreateProductRequestDto;
import gift.product.dto.GetProductResponseDto;
import gift.product.dto.UpdateProductRequestDto;
import gift.product.exception.ProductNotFoundException;
import gift.product.repository.ProductJpaRepository;
import gift.product.validation.ProductValidator;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductJpaRepository productRepository;
    private final ProductValidator productValidator;

    public ProductService(ProductJpaRepository productRepository,
        ProductValidator productValidator) {
        this.productRepository = productRepository;
        this.productValidator = productValidator;
    }

    @Transactional(readOnly = true)
    public PageResponseDto<GetProductResponseDto> getAllByPage(PageRequestDto pageRequestDto)
        throws IllegalArgumentException {
        productValidator.validateProductSortField(pageRequestDto.sortInfo().sortField());

        Page<Product> pagedProduct = productRepository.findAll(pageRequestDto.toPageable());
        Page<GetProductResponseDto> pagedDto = pagedProduct.map(GetProductResponseDto::from);
        return PageResponseDto.from(pagedDto);
    }

    @Transactional(readOnly = true)
    public GetProductResponseDto getProductById(Long id) throws ProductNotFoundException {
        Product product = findProductOrThrow(id);

        return GetProductResponseDto.from(product);
    }

    @Transactional
    public Long createProduct(CreateProductRequestDto dto) {
        productValidator.validateProductName(dto.name());

        Product newProduct = Product.of(
            dto.name(),
            dto.price(),
            dto.description(),
            dto.imageUrl()
        );

        return productRepository.save(newProduct).getId();
    }

    @Transactional
    public void updateProduct(Long id, UpdateProductRequestDto dto)
        throws ProductNotFoundException {
        productValidator.validateProductName(dto.name());

        Product foundProduct = findProductOrThrow(id);
        foundProduct.update(dto.name(), dto.price(), dto.description(), dto.imageUrl());
    }

    @Transactional
    public void deleteProduct(Long id) {
        findProductOrThrow(id);

        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Product findProductOrThrow(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));
    }
}
