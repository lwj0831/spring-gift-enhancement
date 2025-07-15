package gift.member.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import gift.member.domain.Member;
import java.util.Optional;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
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
class JdbcMemberRepositoryTest {

  @Mock
  private NamedParameterJdbcTemplate jdbcTemplate;

  @Mock
  private SimpleJdbcInsert jdbcInsert;

  private JdbcMemberRepository repository;

  @BeforeEach
  void setUp() {
    repository = new JdbcMemberRepository(jdbcTemplate, jdbcInsert);
  }

  @Test
  void 멤버를_저장하면_ID가_반환된다() {
    Member member = Member.of("kim");
    Long expectedId = 1L;

    when(jdbcInsert.executeAndReturnKey(any(SqlParameterSource.class)))
        .thenReturn(expectedId);

    Long result = repository.save(member);

    assertAll(
        ()->assertEquals(expectedId, result),
        ()->verify(jdbcInsert).executeAndReturnKey(any(SqlParameterSource.class))
    );
  }

  @Test
  void 멤버_저장시_null이면_예외가_발생한다() {
    assertAll(
        ()->assertThrows(NullPointerException.class, () -> repository.save(null)),
        ()->verify(jdbcInsert, never()).executeAndReturnKey(any(SqlParameterSource.class))
    );

  }

  @Test
  void ID로_멤버를_정상_조회할_수_있다() {
    Long id = 1L;
    Member expectedMember = Member.withId(id, "kim");
    String expectedSql = "SELECT * FROM member WHERE id = :id";

    when(jdbcTemplate.queryForObject(eq(expectedSql), any(Map.class), any(RowMapper.class)))
        .thenReturn(expectedMember);

    Optional<Member> result = repository.findById(id);

    assertAll(
        ()->assertTrue(result.isPresent()),
        ()->assertEquals(expectedMember, result.get()),
        ()->verify(jdbcTemplate).queryForObject(eq(expectedSql), any(Map.class), any(RowMapper.class))
    );
  }

  @Test
  void 존재하지_않는_ID로_조회하면_빈_Optional을_반환한다() {
    Long id = 999L;
    String expectedSql = "SELECT * FROM member WHERE id = :id";

    when(jdbcTemplate.queryForObject(eq(expectedSql), any(Map.class), any(RowMapper.class)))
        .thenThrow(new EmptyResultDataAccessException(1));

    Optional<Member> result = repository.findById(id);

    assertAll(
        ()->assertFalse(result.isPresent()),
        ()->verify(jdbcTemplate).queryForObject(eq(expectedSql), any(Map.class), any(RowMapper.class))
    );
  }

  @Test
  void ID가_null이면_조회시_예외가_발생한다() {
    assertAll(
        ()->assertThrows(NullPointerException.class, () -> repository.findById(null)),
        ()->verify(jdbcTemplate, never()).queryForObject(any(), any(Map.class), any(RowMapper.class))
    );
  }

  @Test
  void 멤버를_업데이트할_수_있다() {
    Long id = 1L;
    Member updatedMember = Member.of("kim");
    String expectedSql = "UPDATE member SET name = :name WHERE id = :id";

    when(jdbcTemplate.update(eq(expectedSql), any(SqlParameterSource.class)))
        .thenReturn(1);

    assertAll(
        ()->assertDoesNotThrow(() -> repository.update(id, updatedMember)),
        ()->verify(jdbcTemplate).update(eq(expectedSql), any(SqlParameterSource.class))
    );
  }

  @Test
  void 멤버_업데이트시_영향받은_행이_없으면_예외가_발생한다() {
    Long id = 999L;
    Member updatedMember = Member.of("kim");
    String expectedSql = "UPDATE member SET name = :name WHERE id = :id";

    when(jdbcTemplate.update(eq(expectedSql), any(SqlParameterSource.class)))
        .thenReturn(0);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> repository.update(id, updatedMember)
    );
    assertAll(
        ()->assertEquals("member 수정 실패", exception.getMessage()),
        ()->verify(jdbcTemplate).update(eq(expectedSql), any(SqlParameterSource.class))
    );
  }

  @Test
  void 멤버_업데이트시_ID가_null이면_예외가_발생한다() {
    Member updatedMember = Member.of("kim");
    assertAll(
        ()->assertThrows(NullPointerException.class, () -> repository.update(null, updatedMember)),
        ()->verify(jdbcTemplate, never()).update(any(), any(SqlParameterSource.class))
    );
  }

  @Test
  void 멤버_업데이트시_객체가_null이면_예외가_발생한다() {
    Long id = 1L;
    assertAll(
        ()->assertThrows(NullPointerException.class, () -> repository.update(id, null)),
        ()->verify(jdbcTemplate, never()).update(any(), any(SqlParameterSource.class))
    );
  }

  @Test
  void ID로_멤버를_삭제할_수_있다() {
    Long id = 1L;
    String expectedSql = "DELETE FROM member WHERE id = :id";

    when(jdbcTemplate.update(eq(expectedSql), any(SqlParameterSource.class)))
        .thenReturn(1);

    assertAll(
        ()->assertDoesNotThrow(() -> repository.delete(id)),
        ()->verify(jdbcTemplate).update(eq(expectedSql), any(SqlParameterSource.class))
    );
  }

  @Test
  void 멤버_삭제시_영향받은_행이_없으면_예외가_발생한다() {
    Long id = 999L;
    String expectedSql = "DELETE FROM member WHERE id = :id";

    when(jdbcTemplate.update(eq(expectedSql), any(SqlParameterSource.class)))
        .thenReturn(0);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> repository.delete(id)
    );
    assertAll(
        ()->assertEquals("member 삭제 실패", exception.getMessage()),
        ()->verify(jdbcTemplate).update(eq(expectedSql), any(SqlParameterSource.class))
    );
  }

  @Test
  void 멤버_삭제시_ID가_null이면_예외가_발생한다() {
    assertAll(
        ()->assertThrows(NullPointerException.class, () -> repository.delete(null)),
        ()->verify(jdbcTemplate, never()).update(any(), any(SqlParameterSource.class))
    );
  }
}
