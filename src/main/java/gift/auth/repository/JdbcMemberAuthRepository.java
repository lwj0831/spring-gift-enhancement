package gift.auth.repository;

import gift.auth.domain.MemberAuth;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcMemberAuthRepository implements MemberAuthRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private static final RowMapper<MemberAuth> memberAuthRowMapper = (rs, rowNum) ->
      MemberAuth.withId(
          rs.getLong("member_id"),
          rs.getString("email"),
          rs.getString("password"),
          rs.getString("refresh_token")
      );

  @Autowired
  public JdbcMemberAuthRepository(DataSource dataSource) {
    this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
  }

  public JdbcMemberAuthRepository(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Long save(MemberAuth memberAuth) {
    Objects.requireNonNull(memberAuth, "memberAuth는 null일 수 없습니다.");

    String sql = "INSERT INTO member_auth (member_id, email, password, refresh_token) " +
        "VALUES (:memberId, :email, :password, :refreshToken)";

    SqlParameterSource params = new MapSqlParameterSource()
        .addValue("memberId", memberAuth.memberId())
        .addValue("email", memberAuth.email().getEmailText())
        .addValue("password", memberAuth.password())
        .addValue("refreshToken", memberAuth.refreshToken());

    int affected = jdbcTemplate.update(sql, params);
    if (affected == 0) {
      throw new IllegalStateException("memberAuth 저장 실패");
    }
    return memberAuth.memberId();
  }

  @Override
  public Optional<MemberAuth> findById(Long memberId) {
    Objects.requireNonNull(memberId, "ID은 null이 될 수 없습니다.");
    String sql = "SELECT * FROM member_auth WHERE member_id = :memberId";
    try {
      Map<String, Object> params = Map.of("memberId", memberId);
      return Optional.of(jdbcTemplate.queryForObject(sql, params, memberAuthRowMapper));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<MemberAuth> findByEmail(String email) {
    Objects.requireNonNull(email, "Email은 null이 될 수 없습니다.");
    String sql = "SELECT * FROM member_auth WHERE email = :email";
    try {
      Map<String, Object> params = Map.of("email", email);
      return Optional.of(jdbcTemplate.queryForObject(sql, params, memberAuthRowMapper));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public void update(Long memberId, MemberAuth updatedMemberAuth) {
    Objects.requireNonNull(memberId, "ID는 null일 수 없습니다");
    Objects.requireNonNull(updatedMemberAuth, "updatedMember는 null일 수 없습니다");

    String sql =
        "UPDATE member_auth SET member_id = :memberId, email = :email, password = :password, refresh_token = :refreshToken "
            +
            "WHERE member_id = :memberId";

    SqlParameterSource params = new MapSqlParameterSource()
        .addValue("memberId", memberId)
        .addValue("email", updatedMemberAuth.email())
        .addValue("password", updatedMemberAuth.password())
        .addValue("refreshToken", updatedMemberAuth.refreshToken());

    int affected = jdbcTemplate.update(sql, params);
    if (affected == 0) {
      throw new IllegalArgumentException("memberAuth 수정 실패");
    }
  }

  @Override
  public void updateRefreshToken(Long memberId, String newRefreshToken) {
    Objects.requireNonNull(memberId, "ID는 null일 수 없습니다");

    String sql =
        "UPDATE member_auth SET refresh_token = :refreshToken "
            +
            "WHERE member_id = :memberId";

    SqlParameterSource params = new MapSqlParameterSource()
        .addValue("memberId", memberId)
        .addValue("refreshToken", newRefreshToken);

    int affected = jdbcTemplate.update(sql, params);
    if (affected == 0) {
      throw new IllegalArgumentException("memberAuth 수정 실패");
    }
  }

  @Override
  public void delete(Long memberId) {
    Objects.requireNonNull(memberId, "ID는 null일 수 없습니다");

    String sql = "DELETE FROM member_auth WHERE member_id = :memberId";
    SqlParameterSource params = new MapSqlParameterSource()
        .addValue("memberId", memberId);

    int affected = jdbcTemplate.update(sql, params);
    if (affected == 0) {
      throw new IllegalArgumentException("memberAuth 삭제 실패");
    }
  }


}
