package gift.product.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.global.common.dto.SortInfo;
import gift.product.domain.Product;
import gift.product.exception.ProductNotFoundException;
import gift.product.jpa.ProductEntity;
import gift.product.jpa.ProductMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class JpaProductRepositoryAdapterTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private JpaProductRepository jpaProductRepository;

  private JpaProductRepositoryAdapter productRepositoryAdapter;

  @BeforeEach
  void setUp() {
    productRepositoryAdapter = new JpaProductRepositoryAdapter(jpaProductRepository);
  }

  @Test
  @DisplayName("상품을 저장하고 ID를 반환한다")
  void save_ShouldReturnId() {
    Product product = createProduct("테스트 상품", 10000, "테스트 상품 설명", "http://test.com/image.jpg");

    Long savedId = productRepositoryAdapter.save(product);

    ProductEntity savedEntity = entityManager.find(ProductEntity.class, savedId);

    assertAll(
        () -> assertThat(savedId).isNotNull(),
        () -> assertThat(savedId).isGreaterThan(0),
        () -> assertThat(savedEntity).isNotNull(),
        () -> assertThat(savedEntity.getName()).isEqualTo("테스트 상품"),
        () -> assertThat(savedEntity.getPrice()).isEqualTo(10000),
        () -> assertThat(savedEntity.getDescription()).isEqualTo("테스트 상품 설명")
    );
  }

  @Test
  @DisplayName("ID로 상품을 조회한다")
  void findById_ShouldReturnProduct() {
    ProductEntity savedEntity = createAndSaveProductEntity("테스트 상품", 10000, "테스트 상품 설명",
        "http://test.com/image.jpg");

    Optional<Product> foundProduct = productRepositoryAdapter.findById(savedEntity.getId());

    assertAll(
        () -> assertThat(foundProduct).isPresent(),
        () -> assertThat(foundProduct.get().name()).isEqualTo("테스트 상품"),
        () -> assertThat(foundProduct.get().price()).isEqualTo(10000),
        () -> assertThat(foundProduct.get().description()).isEqualTo("테스트 상품 설명")
    );
  }

  @Test
  @DisplayName("존재하지 않는 ID로 조회시 빈 Optional을 반환한다")
  void findById_WithNonExistentId_ShouldReturnEmptyOptional() {
    Long nonExistentId = 999L;

    Optional<Product> foundProduct = productRepositoryAdapter.findById(nonExistentId);

    assertThat(foundProduct).isEmpty();
  }

  @Test
  @DisplayName("모든 상품을 조회한다")
  void findAll_ShouldReturnAllProducts() {
    createAndSaveProductEntity("상품1", 10000, "상품1 설명", "http://test.com/image.jpg");
    createAndSaveProductEntity("상품2", 20000, "상품2 설명", "http://test.com/image.jpg");
    createAndSaveProductEntity("상품3", 30000, "상품3 설명", "http://test.com/image.jpg");

    List<Product> allProducts = productRepositoryAdapter.findAll();

    assertAll(
        () -> assertThat(allProducts).hasSize(3),
        () -> assertThat(allProducts)
            .extracting(Product::name)
            .containsExactlyInAnyOrder("상품1", "상품2", "상품3")
    );
  }

  @Test
  @DisplayName("페이지네이션과 정렬을 적용하여 상품을 조회한다 - 가격 오름차순")
  void findAllByPage_WithPriceAscending_ShouldReturnSortedProducts() {
    createAndSaveProductEntity("상품1", 30000, "상품1 설명", "http://test.com/image.jpg");
    createAndSaveProductEntity("상품2", 10000, "상품2 설명", "http://test.com/image.jpg");
    createAndSaveProductEntity("상품3", 20000, "상품3 설명", "http://test.com/image.jpg");
    createAndSaveProductEntity("상품4", 40000, "상품4 설명", "http://test.com/image.jpg");

    SortInfo sortInfo = new SortInfo("price", true);

    List<Product> products = productRepositoryAdapter.findAllByPage(0, 2, sortInfo);

    assertAll(
        () -> assertThat(products).hasSize(2),
        () -> assertThat(products.get(0).price()).isEqualTo(10000),
        () -> assertThat(products.get(1).price()).isEqualTo(20000)
    );
  }

  @Test
  @DisplayName("페이지네이션과 정렬을 적용하여 상품을 조회한다 - 가격 내림차순")
  void findAllByPage_WithPriceDescending_ShouldReturnSortedProducts() {
    createAndSaveProductEntity("상품1", 30000, "상품1 설명", "http://test.com/image.jpg");
    createAndSaveProductEntity("상품2", 10000, "상품2 설명", "http://test.com/image.jpg");
    createAndSaveProductEntity("상품3", 20000, "상품3 설명", "http://test.com/image.jpg");
    createAndSaveProductEntity("상품4", 40000, "상품4 설명", "http://test.com/image.jpg");

    SortInfo sortInfo = new SortInfo("price", false);

    List<Product> products = productRepositoryAdapter.findAllByPage(0, 2, sortInfo);

    assertAll(
        () -> assertThat(products).hasSize(2),
        () -> assertThat(products.get(0).price()).isEqualTo(40000),
        () -> assertThat(products.get(1).price()).isEqualTo(30000)
    );
  }

  @Test
  @DisplayName("페이지네이션과 정렬을 적용하여 상품을 조회한다 - 이름 오름차순")
  void findAllByPage_WithNameAscending_ShouldReturnSortedProducts() {
    createAndSaveProductEntity("C상품", 10000, "C상품 설명", "http://test.com/image.jpg");
    createAndSaveProductEntity("A상품", 20000, "A상품 설명", "http://test.com/image.jpg");
    createAndSaveProductEntity("B상품", 30000, "B상품 설명", "http://test.com/image.jpg");

    SortInfo sortInfo = new SortInfo("name", true);

    List<Product> products = productRepositoryAdapter.findAllByPage(0, 3, sortInfo);

    assertAll(
        () -> assertThat(products).hasSize(3),
        () -> assertThat(products.get(0).name()).isEqualTo("A상품"),
        () -> assertThat(products.get(1).name()).isEqualTo("B상품"),
        () -> assertThat(products.get(2).name()).isEqualTo("C상품")
    );
  }

  @Test
  @DisplayName("상품을 업데이트한다")
  void update_ShouldUpdateProduct() {
    ProductEntity savedEntity = createAndSaveProductEntity("원본 상품", 10000, "원본 설명",
        "http://test.com/image.jpg");
    Product updateProduct = createProduct("수정된 상품", 20000, "수정된 설명", "http://test.com/image.jpg");

    productRepositoryAdapter.update(savedEntity.getId(), updateProduct);
    entityManager.flush();
    entityManager.clear();

    ProductEntity updatedEntity = entityManager.find(ProductEntity.class, savedEntity.getId());

    assertAll(
        () -> assertThat(updatedEntity.getName()).isEqualTo("수정된 상품"),
        () -> assertThat(updatedEntity.getPrice()).isEqualTo(20000),
        () -> assertThat(updatedEntity.getDescription()).isEqualTo("수정된 설명")
    );
  }

  @Test
  @DisplayName("존재하지 않는 상품을 업데이트하면 예외가 발생한다")
  void update_WithNonExistentId_ShouldThrowException() {
    Long nonExistentId = 999L;
    Product updateProduct = createProduct("수정된 상품", 20000, "수정된 설명", "http://test.com/image.jpg");

    assertThatThrownBy(() -> productRepositoryAdapter.update(nonExistentId, updateProduct))
        .isInstanceOf(ProductNotFoundException.class);
  }

  @Test
  @DisplayName("상품을 삭제한다")
  void deleteById_ShouldDeleteProduct() {
    ProductEntity savedEntity = createAndSaveProductEntity("삭제할 상품", 10000, "삭제할 상품 설명",
        "http://test.com/image.jpg");
    Long productId = savedEntity.getId();

    productRepositoryAdapter.deleteById(productId);
    entityManager.flush();

    ProductEntity deletedEntity = entityManager.find(ProductEntity.class, productId);

    assertThat(deletedEntity).isNull();
  }

  @Test
  @DisplayName("존재하지 않는 상품을 삭제해도 예외가 발생하지 않는다")
  void deleteById_WithNonExistentId_ShouldNotThrowException() {
    Long nonExistentId = 999L;

    productRepositoryAdapter.deleteById(nonExistentId);
  }

  private Product createProduct(String name, int price, String description, String imageUrl) {
    return Product.of(name, price, description, imageUrl);
  }

  private ProductEntity createAndSaveProductEntity(String name, int price, String description,
      String imageUrl) {
    Product product = createProduct(name, price, description, imageUrl);
    ProductEntity entity = ProductMapper.toEntity(product);
    return entityManager.persistAndFlush(entity);
  }
}
