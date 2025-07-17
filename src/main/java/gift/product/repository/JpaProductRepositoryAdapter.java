package gift.product.repository;

import gift.global.common.dto.SortInfo;
import gift.product.domain.Product;
import gift.product.exception.ProductNotFoundException;
import gift.product.jpa.ProductEntity;
import gift.product.jpa.ProductMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public class JpaProductRepositoryAdapter implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;

    public JpaProductRepositoryAdapter(JpaProductRepository jpaProductRepository) {
        this.jpaProductRepository = jpaProductRepository;
    }

    @Override
    public Long save(Product product) {
        ProductEntity savedProductEntity = jpaProductRepository.save(
            ProductMapper.toEntity(product));
        return savedProductEntity.getId();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaProductRepository.findById(id).map(ProductMapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return jpaProductRepository.findAll().stream()
            .map(ProductMapper::toDomain)
            .toList();
    }

    @Override
    public List<Product> findAllByPage(int offset, int pageSize, SortInfo sortInfo) {
        Direction direction = sortInfo.isAscending() ? Direction.ASC : Direction.DESC;
        Sort sort = Sort.by(direction, sortInfo.field());
        return jpaProductRepository.findAll(PageRequest.of(offset, pageSize, sort)).stream()
            .map(ProductMapper::toDomain)
            .toList();
    }

    @Override
    public void update(Long id, Product updateProduct) {
        ProductEntity productEntity = jpaProductRepository.findById(id).orElseThrow(
            ProductNotFoundException::new);
        productEntity.updateFromDomain(updateProduct);
    }

    @Override
    public void deleteById(Long id) {
        jpaProductRepository.deleteById(id);
    }
}
