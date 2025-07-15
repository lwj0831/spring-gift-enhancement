package gift.product.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import gift.global.common.dto.SortInfo;
import gift.product.domain.Product;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

@ExtendWith(MockitoExtension.class)
class JdbcProductRepositoryTest {

  @Mock
  private NamedParameterJdbcTemplate jdbcTemplate;

  @Mock
  private SimpleJdbcInsert jdbcInsert;

  private JdbcProductRepository repository;
  private Product testProduct;

  @BeforeEach
  void setUp() {
    repository = new JdbcProductRepository(jdbcTemplate,jdbcInsert);

    testProduct = new Product(null, "테스트 상품", 10000, "테스트 상품 설명", "http://test.com/image.jpg");
  }

  @Test
  void 상품을_저장하면_ID를_반환한다() {
    given(jdbcInsert.executeAndReturnKey(any(SqlParameterSource.class)))
        .willReturn(1L);

    Long savedId = repository.save(testProduct);

    assertAll(
        () -> assertThat(savedId).isNotNull(),
        () -> assertThat(savedId).isEqualTo(1L)
    );
  }

  @Test
  void 상품을_저장할_때_null이면_예외가_발생한다() {
    assertThatThrownBy(() -> repository.save(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("상품은 null이 될 수 없습니다.");
  }

  @Test
  void ID로_상품을_조회할_수_있다() {
    Long productId = 1L;
    Product expectedProduct = new Product(1L, "테스트 상품", 10000, "테스트 상품 설명", "http://test.com/image.jpg");

    given(jdbcTemplate.queryForObject(
        eq("SELECT * FROM product WHERE id = :id"),
        eq(Map.of("id", productId)),
        any(RowMapper.class)
    )).willReturn(expectedProduct);

    Optional<Product> foundProduct = repository.findById(productId);

    assertAll(
        () -> assertThat(foundProduct).isPresent(),
        () -> assertThat(foundProduct.get().name()).isEqualTo("테스트 상품"),
        () -> assertThat(foundProduct.get().price()).isEqualTo(10000),
        () -> assertThat(foundProduct.get().description()).isEqualTo("테스트 상품 설명"),
        () -> assertThat(foundProduct.get().imageUrl()).isEqualTo("http://test.com/image.jpg")
    );
  }

  @Test
  void 존재하지_않는_ID로_조회하면_빈_Optional을_반환한다() {
    Long productId = 999L;

    given(jdbcTemplate.queryForObject(
        eq("SELECT * FROM product WHERE id = :id"),
        eq(Map.of("id", productId)),
        any(RowMapper.class)
    )).willThrow(new EmptyResultDataAccessException(1));

    Optional<Product> foundProduct = repository.findById(productId);

    assertThat(foundProduct).isEmpty();
  }

  @Test
  void ID가_null이면_조회시_예외가_발생한다() {
    assertThatThrownBy(() -> repository.findById(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("ID은 null이 될 수 없습니다.");
  }

  @Test
  void 모든_상품을_조회할_수_있다() {
    List<Product> expectedProducts = List.of(
        new Product(1L, "상품1", 10000, "설명1", "image1.jpg"),
        new Product(2L, "상품2", 20000, "설명2", "image2.jpg")
    );

    given(jdbcTemplate.query(
        eq("SELECT * FROM product"),
        any(RowMapper.class)
    )).willReturn(expectedProducts);

    List<Product> products = repository.findAll();

    assertAll(
        () -> assertThat(products).hasSize(2),
        () -> assertThat(products.get(0).name()).isEqualTo("상품1"),
        () -> assertThat(products.get(1).name()).isEqualTo("상품2")
    );
  }

  @Test
  void 모든_상품을_페이징하여_조회할_수_있다() {
    SortInfo sortInfo = new SortInfo("name", true);
    List<Product> expectedProducts = List.of(
        new Product(1L, "상품1", 10000, "설명1", "image1.jpg"),
        new Product(2L, "상품2", 20000, "설명2", "image2.jpg")
    );

    given(jdbcTemplate.query(
        eq("SELECT * FROM product ORDER BY name ASC LIMIT :limit OFFSET :offset"),
        any(MapSqlParameterSource.class),
        any(RowMapper.class)
    )).willReturn(expectedProducts);

    List<Product> products = repository.findAllByPage(0, 10, sortInfo);

    assertThat(products).hasSize(2);
  }

  @Test
  void 상품을_업데이트할_수_있다() {
    Long productId = 1L;
    Product updatedProduct = new Product(productId, "수정된 상품", 15000, "수정된 설명", "http://test.com/updated.jpg");

    given(jdbcTemplate.update(
        eq("UPDATE product SET name = :name, price = :price, description = :description, image_url = :imageUrl WHERE id = :id"),
        any(SqlParameterSource.class)
    )).willReturn(1);

    repository.update(productId, updatedProduct);

    verify(jdbcTemplate).update(
        eq("UPDATE product SET name = :name, price = :price, description = :description, image_url = :imageUrl WHERE id = :id"),
        any(SqlParameterSource.class)
    );
  }

  @Test
  void 업데이트시_존재하지_않는_상품이면_예외가_발생한다() {
    Long productId = 999L;
    Product updatedProduct = new Product(productId, "수정된 상품", 15000, "수정된 설명", "updated.jpg");

    given(jdbcTemplate.update(anyString(), any(SqlParameterSource.class)))
        .willReturn(0);

    assertThatThrownBy(() -> repository.update(productId, updatedProduct))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("수정 실패");
  }

  @Test
  void 업데이트시_ID가_null이면_예외가_발생한다() {
    assertThatThrownBy(() -> repository.update(null, testProduct))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("ID는 null일 수 없습니다");
  }

  @Test
  void 업데이트시_상품이_null이면_예외가_발생한다() {
    assertThatThrownBy(() -> repository.update(1L, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("상품은 null일 수 없습니다");
  }

  @Test
  void ID로_상품을_삭제할_수_있다() {
    Long productId = 1L;

    given(jdbcTemplate.update(
        eq("DELETE FROM product WHERE id = :id"),
        any(SqlParameterSource.class)
    )).willReturn(1);

    repository.deleteById(productId);

    verify(jdbcTemplate).update(
        eq("DELETE FROM product WHERE id = :id"),
        any(SqlParameterSource.class)
    );
  }

  @Test
  void 삭제시_존재하지_않는_상품이면_예외가_발생한다() {
    Long productId = 999L;

    given(jdbcTemplate.update(anyString(), any(SqlParameterSource.class)))
        .willReturn(0);

    assertThatThrownBy(() -> repository.deleteById(productId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("삭제 실패");
  }

  @Test
  void 삭제시_ID가_null이면_예외가_발생한다() {
    assertThatThrownBy(() -> repository.deleteById(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("ID는 null일 수 없습니다");
  }

  @Test
  void 빈_저장소에서_전체_조회하면_빈_리스트를_반환한다() {
    given(jdbcTemplate.query(
        eq("SELECT * FROM product"),
        any(RowMapper.class)
    )).willReturn(List.of());

    List<Product> products = repository.findAll();

    assertThat(products).isEmpty();
  }

  @Test
  void 페이지_크기보다_적은_데이터가_있을_때_실제_데이터만_반환한다() {
    SortInfo sortInfo = new SortInfo("name", true);
    List<Product> expectedProducts = List.of(
        new Product(1L, "상품1", 10000, "설명1", "image1.jpg")
    );

    given(jdbcTemplate.query(
        eq("SELECT * FROM product ORDER BY name ASC LIMIT :limit OFFSET :offset"),
        any(MapSqlParameterSource.class),
        any(RowMapper.class)
    )).willReturn(expectedProducts);

    List<Product> products = repository.findAllByPage(0, 10, sortInfo);

    assertThat(products).hasSize(1);
  }

  @Test
  void 저장된_상품의_ID가_올바르게_설정된다() {
    Long expectedId = 1L;
    Product expectedProduct = new Product(expectedId, "테스트 상품", 10000, "테스트 상품 설명", "http://test.com/image.jpg");

    given(jdbcInsert.executeAndReturnKey(any(SqlParameterSource.class)))
        .willReturn(expectedId);
    given(jdbcTemplate.queryForObject(
        eq("SELECT * FROM product WHERE id = :id"),
        eq(Map.of("id", expectedId)),
        any(RowMapper.class)
    )).willReturn(expectedProduct);

    Long savedId = repository.save(testProduct);
    Optional<Product> foundProduct = repository.findById(savedId);

    assertAll(
        () -> assertThat(foundProduct).isPresent(),
        () -> assertThat(foundProduct.get().id()).isEqualTo(savedId)
    );
  }

  @Test
  void 페이지네이션_정렬_내림차순으로_조회할_수_있다() {
    SortInfo sortInfo = new SortInfo("price", false);
    List<Product> expectedProducts = List.of(
        new Product(2L, "상품2", 20000, "설명2", "image2.jpg"),
        new Product(1L, "상품1", 10000, "설명1", "image1.jpg")
    );

    given(jdbcTemplate.query(
        eq("SELECT * FROM product ORDER BY price DESC LIMIT :limit OFFSET :offset"),
        any(MapSqlParameterSource.class),
        any(RowMapper.class)
    )).willReturn(expectedProducts);

    List<Product> products = repository.findAllByPage(0, 10, sortInfo);

    assertAll(
        () -> assertThat(products).hasSize(2),
        () -> assertThat(products.get(0).price()).isEqualTo(20000),
        () -> assertThat(products.get(1).price()).isEqualTo(10000)
    );
  }

  @Test
  void 페이지네이션_offset과_limit이_올바르게_전달된다() {
    SortInfo sortInfo = new SortInfo("name", true);
    int offset = 10;
    int pageSize = 5;

    given(jdbcTemplate.query(
        eq("SELECT * FROM product ORDER BY name ASC LIMIT :limit OFFSET :offset"),
        any(MapSqlParameterSource.class),
        any(RowMapper.class)
    )).willReturn(List.of());

    repository.findAllByPage(offset, pageSize, sortInfo);

    verify(jdbcTemplate).query(
        eq("SELECT * FROM product ORDER BY name ASC LIMIT :limit OFFSET :offset"),
        argThat((SqlParameterSource paramSource) -> {
          Object limit = paramSource.getValue("limit");
          Object offsetValue = paramSource.getValue("offset");

          return Objects.equals(limit, pageSize + 1)
              && Objects.equals(offsetValue, offset);
        }),
        any(RowMapper.class)
    );
  }
}