package gift.auth.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import gift.auth.domain.MemberAuth;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

@ExtendWith(MockitoExtension.class)
class JdbcMemberAuthRepositoryTest {

  @Mock
  private NamedParameterJdbcTemplate jdbcTemplate;

  private JdbcMemberAuthRepository repository;

  @BeforeEach
  void setUp() {
    repository = new JdbcMemberAuthRepository(jdbcTemplate);
  }

  @Test
  @DisplayName("memberAuth를 저장하면 memberId가 반환된다")
  void save_success() {
    MemberAuth memberAuth = MemberAuth.withId(1L,"test@example.com", "password", "token");
    Long expectedId = 1L;

    when(jdbcTemplate.update(anyString(), any(SqlParameterSource.class)))
        .thenReturn(1);

    Long result = repository.save(memberAuth);

    assertAll(
        () -> assertEquals(expectedId, result),
        () -> verify(jdbcTemplate).update(anyString(), any(SqlParameterSource.class))
    );
  }

  @Test
  @DisplayName("null 저장시 NullPointerException 발생")
  void save_null_throws() {
    assertAll(
        () -> assertThrows(NullPointerException.class, () -> repository.save(null)),
        () -> verify(jdbcTemplate, never()).update(anyString(), any(SqlParameterSource.class))
    );
  }

  @Test
  @DisplayName("ID로 memberAuth 조회 성공")
  void findById_success() {
    Long memberId = 1L;
    MemberAuth expected = MemberAuth.withId(memberId,"test@example.com", "password", "token");
    String sql = "SELECT * FROM member_auth WHERE member_id = :memberId";

    when(jdbcTemplate.queryForObject(eq(sql), any(Map.class), any(RowMapper.class)))
        .thenReturn(expected);

    Optional<MemberAuth> result = repository.findById(memberId);

    assertAll(
        () -> assertTrue(result.isPresent()),
        () -> assertEquals(expected, result.get()),
        () -> verify(jdbcTemplate).queryForObject(eq(sql), any(Map.class), any(RowMapper.class))
    );
  }

  @Test
  @DisplayName("존재하지 않는 ID 조회 시 빈 Optional 반환")
  void findById_not_found() {
    Long memberId = 999L;
    String sql = "SELECT * FROM member_auth WHERE member_id = :memberId";

    when(jdbcTemplate.queryForObject(eq(sql), any(Map.class), any(RowMapper.class)))
        .thenThrow(new EmptyResultDataAccessException(1));

    Optional<MemberAuth> result = repository.findById(memberId);

    assertAll(
        () -> assertFalse(result.isPresent()),
        () -> verify(jdbcTemplate).queryForObject(eq(sql), any(Map.class), any(RowMapper.class))
    );
  }

  @Test
  @DisplayName("조회 시 ID가 null이면 예외 발생")
  void findById_null_id() {
    assertAll(
        () -> assertThrows(NullPointerException.class, () -> repository.findById(null)),
        () -> verify(jdbcTemplate, never()).queryForObject(any(), any(Map.class), any(RowMapper.class))
    );
  }

  @Test
  @DisplayName("memberAuth를 수정할 수 있다")
  void update_success() {
    Long memberId = 1L;
    MemberAuth updated = MemberAuth.withId(memberId, "new@example.com", "newpass", "newtoken");
    String sql = "UPDATE member_auth SET member_id = :memberId, email = :email, password = :password, refresh_token = :refreshToken WHERE member_id = :memberId";

    when(jdbcTemplate.update(eq(sql), any(SqlParameterSource.class))).thenReturn(1);

    assertAll(
        () -> assertDoesNotThrow(() -> repository.update(memberId, updated)),
        () -> verify(jdbcTemplate).update(eq(sql), any(SqlParameterSource.class))
    );
  }

  @Test
  @DisplayName("업데이트 시 영향받은 행이 없으면 예외 발생")
  void update_fail() {
    Long memberId = 999L;
    MemberAuth updated = MemberAuth.withId(memberId, "abcd@gmail.com", "nope", "nope");
    String sql = "UPDATE member_auth SET member_id = :memberId, email = :email, password = :password, refresh_token = :refreshToken WHERE member_id = :memberId";

    when(jdbcTemplate.update(eq(sql), any(SqlParameterSource.class))).thenReturn(0);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> repository.update(memberId, updated));

    assertAll(
        () -> assertEquals("memberAuth 수정 실패", exception.getMessage()),
        () -> verify(jdbcTemplate).update(eq(sql), any(SqlParameterSource.class))
    );
  }

  @Test
  @DisplayName("업데이트 시 ID가 null이면 예외 발생")
  void update_null_id() {
    MemberAuth updated = MemberAuth.withId(1L, "a@a.com", "a", "b");

    assertAll(
        () -> assertThrows(NullPointerException.class, () -> repository.update(null, updated)),
        () -> verify(jdbcTemplate, never()).update(any(), any(SqlParameterSource.class))
    );
  }

  @Test
  @DisplayName("업데이트 시 객체가 null이면 예외 발생")
  void update_null_object() {
    assertAll(
        () -> assertThrows(NullPointerException.class, () -> repository.update(1L, null)),
        () -> verify(jdbcTemplate, never()).update(any(), any(SqlParameterSource.class))
    );
  }

  @Test
  @DisplayName("ID로 memberAuth 삭제 성공")
  void delete_success() {
    Long memberId = 1L;
    String sql = "DELETE FROM member_auth WHERE member_id = :memberId";

    when(jdbcTemplate.update(eq(sql), any(SqlParameterSource.class))).thenReturn(1);

    assertAll(
        () -> assertDoesNotThrow(() -> repository.delete(memberId)),
        () -> verify(jdbcTemplate).update(eq(sql), any(SqlParameterSource.class))
    );
  }

  @Test
  @DisplayName("삭제 시 영향받은 행이 없으면 예외 발생")
  void delete_fail() {
    Long memberId = 999L;
    String sql = "DELETE FROM member_auth WHERE member_id = :memberId";

    when(jdbcTemplate.update(eq(sql), any(SqlParameterSource.class))).thenReturn(0);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> repository.delete(memberId));

    assertAll(
        () -> assertEquals("memberAuth 삭제 실패", exception.getMessage()),
        () -> verify(jdbcTemplate).update(eq(sql), any(SqlParameterSource.class))
    );
  }

  @Test
  @DisplayName("삭제 시 ID가 null이면 예외 발생")
  void delete_null_id() {
    assertAll(
        () -> assertThrows(NullPointerException.class, () -> repository.delete(null)),
        () -> verify(jdbcTemplate, never()).update(any(), any(SqlParameterSource.class))
    );
  }
}
