package gift.member.repository;

import gift.member.domain.Member;
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
public class JdbcMemberRepository implements MemberRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final SimpleJdbcInsert jdbcInsert;
  private static final RowMapper<Member> memberRowMapper = (rs, rowNum) ->
      Member.withId(
          rs.getLong("id"),
          rs.getString("name")
      );

  @Autowired
  public JdbcMemberRepository(DataSource dataSource) {
    this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    this.jdbcInsert = new SimpleJdbcInsert(dataSource)
        .withTableName("member")
        .usingGeneratedKeyColumns("id");
  }

  public JdbcMemberRepository(NamedParameterJdbcTemplate jdbcTemplate,
      SimpleJdbcInsert jdbcInsert) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcInsert = jdbcInsert;
  }

  @Override
  public Long save(Member member) {
    Objects.requireNonNull(member, "member는 null일 수 없습니다.");
    SqlParameterSource params = new BeanPropertySqlParameterSource(member);
    Number key = jdbcInsert.executeAndReturnKey(params);
    return key.longValue();
  }

  @Override
  public Optional<Member> findById(Long id) {
    Objects.requireNonNull(id, "ID은 null이 될 수 없습니다.");
    String sql = "SELECT * FROM member WHERE id = :id";
    try {
      Map<String, Object> params = Map.of("id", id);
      return Optional.of(jdbcTemplate.queryForObject(sql, params, memberRowMapper));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public void update(Long id, Member updatedMember) {
    Objects.requireNonNull(id, "ID는 null일 수 없습니다");
    Objects.requireNonNull(updatedMember, "updatedMember는 null일 수 없습니다");

    String sql =
        "UPDATE member SET name = :name "
            +
            "WHERE id = :id";

    SqlParameterSource params = new MapSqlParameterSource()
        .addValue("id", id)
        .addValue("name", updatedMember.name());

    int affected = jdbcTemplate.update(sql, params);
    if (affected == 0) {
      throw new IllegalArgumentException("member 수정 실패");
    }
  }

  @Override
  public void delete(Long id) {
    Objects.requireNonNull(id, "ID는 null일 수 없습니다");

    String sql = "DELETE FROM member WHERE id = :id";
    SqlParameterSource params = new MapSqlParameterSource()
        .addValue("id", id);

    int affected = jdbcTemplate.update(sql, params);
    if (affected == 0) {
      throw new IllegalArgumentException("member 삭제 실패");
    }
  }
}
