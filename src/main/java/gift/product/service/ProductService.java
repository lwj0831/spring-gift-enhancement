package gift.product.service;

import gift.global.common.dto.PageRequest;
import gift.global.common.dto.PagedResult;
import gift.product.domain.Product;
import gift.product.dto.CreateProductRequestDto;
import gift.product.dto.GetProductResponseDto;
import gift.product.dto.UpdateProductRequestDto;
import gift.product.exception.ProductNotFoundException;
import gift.product.repository.ProductRepository;
import gift.product.validation.ProductValidator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductValidator productValidator;

  public ProductService(ProductRepository productRepository, ProductValidator productValidator) {
    this.productRepository = productRepository;
    this.productValidator = productValidator;
  }

  public PagedResult<GetProductResponseDto> getAllByPage(PageRequest pageRequest)
      throws IllegalArgumentException {
    List<Product> pagedProductList = productRepository.findAllByPage(pageRequest.offset(),
        pageRequest.pageSize(), pageRequest.sortInfo());
    return PagedResult.of(pagedProductList, pageRequest.offset(), pageRequest.pageSize())
        .map(GetProductResponseDto::from);
  }

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
    return productRepository.save(newProduct);
  }

  @Transactional
  public void updateProduct(Long id, UpdateProductRequestDto dto) throws ProductNotFoundException {
    productValidator.validateProductName(dto.name());
    findProductOrThrow(id);
    Product newProduct = Product.withId(
        id,
        dto.name(),
        dto.price(),
        dto.description(),
        dto.imageUrl()
    );
    productRepository.update(id, newProduct);
  }

  @Transactional
  public void deleteProduct(Long id) throws ProductNotFoundException {
    findProductOrThrow(id);
    productRepository.deleteById(id);
  }

  public Product findProductOrThrow(Long productId) {
    return productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
  }
}
