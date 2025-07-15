package gift.wishlist.repository;

import gift.global.common.dto.SortInfo;
import gift.product.dto.SimpleWishItemDto;
import gift.wishlist.domain.WishItem;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcWishItemRepository implements WishItemRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final SimpleJdbcInsert jdbcInsert;
  private static final RowMapper<WishItem> wishItemRowMapper = (rs, rowNum) ->
      WishItem.withId(
          rs.getLong("id"),
          rs.getLong("member_id"),
          rs.getLong("product_id")
      );
  private static final RowMapper<SimpleWishItemDto> simpleWishItemRowMapper = (rs, rowNum) ->
      new SimpleWishItemDto(
          rs.getLong("id"),
          rs.getString("name"),
          rs.getInt("price"),
          rs.getString("image_url")
      );

  @Autowired
  public JdbcWishItemRepository(DataSource dataSource) {
    this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    this.jdbcInsert = new SimpleJdbcInsert(dataSource)
        .withTableName("wish_item")
        .usingGeneratedKeyColumns("id");
  }

  public JdbcWishItemRepository(NamedParameterJdbcTemplate jdbcTemplate,
      SimpleJdbcInsert jdbcInsert) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcInsert = jdbcInsert;
  }

  @Override
  public Long save(WishItem wishItem) {
    Objects.requireNonNull("wishItem는 null일 수 없습니다.");
    SqlParameterSource params = new BeanPropertySqlParameterSource(wishItem);
    Number key = jdbcInsert.executeAndReturnKey(params);
    return key.longValue();
  }

  @Override
  public Optional<WishItem> findById(Long id) {
    Objects.requireNonNull(id, "ID는 null일 수 없습니다.");
    String sql = "SELECT * FROM wish_item WHERE id = :id";
    try {
      Map<String, Object> params = Map.of("id", id);
      return Optional.of(jdbcTemplate.queryForObject(sql, params, wishItemRowMapper));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<WishItem> findByMemberIdAndProductId(Long memberId, Long productId) {
    Objects.requireNonNull(memberId, "회원 id는 null일 수 없습니다.");
    Objects.requireNonNull(productId, "상품 id는 null일 수 없습니다.");
    String sql = "SELECT * FROM wish_item WHERE member_id = :memberId AND product_id = :productId";
    try {
      MapSqlParameterSource params = new MapSqlParameterSource()
          .addValue("memberId", memberId)
          .addValue("productId", productId);
      return Optional.of(jdbcTemplate.queryForObject(sql, params, wishItemRowMapper));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public List<SimpleWishItemDto> findWishItemsWithProductByMemberId(Long memberId) {
    Objects.requireNonNull(memberId, "회원 id는 null일 수 없습니다.");

    String sql = """
        SELECT p.id, p.name, p.price, p.image_url
        FROM wish_item w
        INNER JOIN product p ON w.product_id = p.id
        WHERE w.member_id = :memberId
        """;

    Map<String, Object> params = Map.of("memberId", memberId);

    return jdbcTemplate.query(sql, params, simpleWishItemRowMapper);
  }

  @Override
  public List<WishItem> findAllByMemberId(Long memberId) {
    Objects.requireNonNull(memberId, "회원 id는 null일 수 없습니다.");
    String sql = "SELECT * FROM wish_item WHERE member_id = :memberId";
    Map<String, Object> params = Map.of("memberId", memberId);
    return jdbcTemplate.query(sql, params, wishItemRowMapper);
  }

  @Override
  public List<WishItem> findAllByPage(int offset, int pageSize, SortInfo sortInfo, Long memberId) {
    String sortDirection = sortInfo.isAscending() ? "ASC" : "DESC";
    String sql = String.format(
        "SELECT * FROM wish_item WHERE member_id = :memberId ORDER BY %s %s LIMIT :limit OFFSET :offset",
        sortInfo.field(), sortDirection
    );

    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("memberId", memberId)
        .addValue("limit", pageSize + 1)
        .addValue("offset", offset);

    return jdbcTemplate.query(sql, params, wishItemRowMapper);
  }

  @Override
  public void deleteById(Long id) {
    Objects.requireNonNull(id, "ID는 null일 수 없습니다");

    String sql = "DELETE FROM wish_item WHERE id = :id";
    SqlParameterSource params = new MapSqlParameterSource()
        .addValue("id", id);

    int affected = jdbcTemplate.update(sql, params);
    if (affected == 0) {
      throw new IllegalArgumentException("삭제 실패");
    }
  }
}
