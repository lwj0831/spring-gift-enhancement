package gift.auth.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.auth.domain.MemberAuth;
import gift.auth.jpa.MemberAuthEntity;
import gift.auth.jpa.MemberAuthMapper;
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
class JpaMemberAuthRepositoryAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JpaMemberAuthRepository jpaMemberAuthRepository;

    private JpaMemberAuthRepositoryAdapter memberAuthRepositoryAdapter;

    @BeforeEach
    void setUp() {
        memberAuthRepositoryAdapter = new JpaMemberAuthRepositoryAdapter(jpaMemberAuthRepository);
    }

    @Test
    @DisplayName("회원 인증 정보를 저장하고 ID를 반환한다")
    void save_ShouldReturnId() {
        MemberAuth memberAuth = createMemberAuth(1L, "user@example.com", "encodedPassword",
            "refreshToken");

        Long savedId = memberAuthRepositoryAdapter.save(memberAuth);

        MemberAuthEntity savedEntity = entityManager.find(MemberAuthEntity.class, savedId);

        assertAll(
            () -> assertThat(savedId).isNotNull(),
            () -> assertThat(savedId).isGreaterThan(0),
            () -> assertThat(savedEntity).isNotNull(),
            () -> assertThat(savedEntity.getEmail()).isEqualTo("user@example.com"),
            () -> assertThat(savedEntity.getPassword()).isEqualTo("encodedPassword"),
            () -> assertThat(savedEntity.getRefreshToken()).isEqualTo("refreshToken")
        );
    }

    @Test
    @DisplayName("ID로 회원 인증 정보를 조회한다")
    void findById_ShouldReturnMemberAuth() {
        MemberAuthEntity savedEntity = createAndSaveMemberAuthEntity(1L, "user@example.com",
            "encodedPassword", "refreshToken");

        Optional<MemberAuth> foundMemberAuth = memberAuthRepositoryAdapter.findById(
            savedEntity.getId());

        assertAll(
            () -> assertThat(foundMemberAuth).isPresent(),
            () -> assertThat(foundMemberAuth.get().email().getEmailText()).isEqualTo(
                "user@example.com"),
            () -> assertThat(foundMemberAuth.get().password()).isEqualTo("encodedPassword"),
            () -> assertThat(foundMemberAuth.get().refreshToken()).isEqualTo("refreshToken")
        );
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회시 빈 Optional을 반환한다")
    void findById_WithNonExistentId_ShouldReturnEmptyOptional() {
        Long nonExistentId = 999L;

        Optional<MemberAuth> foundMemberAuth = memberAuthRepositoryAdapter.findById(nonExistentId);

        assertThat(foundMemberAuth).isEmpty();
    }

    @Test
    @DisplayName("이메일로 회원 인증 정보를 조회한다")
    void findByEmail_ShouldReturnMemberAuth() {
        String email = "user@example.com";
        createAndSaveMemberAuthEntity(1L, email, "encodedPassword", "refreshToken");

        Optional<MemberAuth> foundMemberAuth = memberAuthRepositoryAdapter.findByEmail(email);

        assertAll(
            () -> assertThat(foundMemberAuth).isPresent(),
            () -> assertThat(foundMemberAuth.get().email().getEmailText()).isEqualTo(email),
            () -> assertThat(foundMemberAuth.get().password()).isEqualTo("encodedPassword"),
            () -> assertThat(foundMemberAuth.get().refreshToken()).isEqualTo("refreshToken")
        );
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 조회시 빈 Optional을 반환한다")
    void findByEmail_WithNonExistentEmail_ShouldReturnEmptyOptional() {
        String nonExistentEmail = "nonexistent@example.com";

        Optional<MemberAuth> foundMemberAuth = memberAuthRepositoryAdapter.findByEmail(
            nonExistentEmail);

        assertThat(foundMemberAuth).isEmpty();
    }

    @Test
    @DisplayName("회원 인증 정보를 업데이트한다")
    void update_ShouldUpdateMemberAuth() {
        MemberAuthEntity savedEntity = createAndSaveMemberAuthEntity(1L, "user@example.com",
            "oldPassword", "oldRefreshToken");
        MemberAuth updateMemberAuth = createMemberAuth(1L, "user@example.com", "newPassword",
            "newRefreshToken");

        memberAuthRepositoryAdapter.update(savedEntity.getId(), updateMemberAuth);
        entityManager.flush();
        entityManager.clear();

        MemberAuthEntity updatedEntity = entityManager.find(MemberAuthEntity.class,
            savedEntity.getId());

        assertAll(
            () -> assertThat(updatedEntity.getEmail()).isEqualTo("user@example.com"),
            () -> assertThat(updatedEntity.getPassword()).isEqualTo("newPassword"),
            () -> assertThat(updatedEntity.getRefreshToken()).isEqualTo("newRefreshToken")
        );
    }

    @Test
    @DisplayName("존재하지 않는 멤버 인증 정보를 업데이트하면 예외가 발생한다")
    void update_WithNonExistentId_ShouldThrowException() {
        Long nonExistentId = 999L;
        MemberAuth updateMemberAuth = createMemberAuth(1L, "user@example.com", "newPassword",
            "newRefreshToken");

        assertThatThrownBy(
            () -> memberAuthRepositoryAdapter.update(nonExistentId, updateMemberAuth))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("리프레시 토큰을 업데이트한다")
    void updateRefreshToken_ShouldUpdateRefreshToken() {
        MemberAuthEntity savedEntity = createAndSaveMemberAuthEntity(1L, "user@example.com",
            "encodedPassword", "oldRefreshToken");
        String newRefreshToken = "newRefreshToken";

        memberAuthRepositoryAdapter.updateRefreshToken(savedEntity.getId(), newRefreshToken);
        entityManager.flush();
        entityManager.clear();

        MemberAuthEntity updatedEntity = entityManager.find(MemberAuthEntity.class,
            savedEntity.getId());

        assertAll(
            () -> assertThat(updatedEntity.getRefreshToken()).isEqualTo(newRefreshToken),
            () -> assertThat(updatedEntity.getEmail()).isEqualTo("user@example.com"),
            () -> assertThat(updatedEntity.getPassword()).isEqualTo("encodedPassword")
        );
    }

    @Test
    @DisplayName("존재하지 않는 회원의 리프레시 토큰을 업데이트하면 예외가 발생한다")
    void updateRefreshToken_WithNonExistentId_ShouldThrowException() {
        Long nonExistentId = 999L;
        String newRefreshToken = "newRefreshToken";

        assertThatThrownBy(
            () -> memberAuthRepositoryAdapter.updateRefreshToken(nonExistentId, newRefreshToken))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("회원 인증 정보를 삭제한다")
    void delete_ShouldDeleteMemberAuth() {
        MemberAuthEntity savedEntity = createAndSaveMemberAuthEntity(1L, "user@example.com",
            "encodedPassword", "refreshToken");
        Long memberId = savedEntity.getId();

        memberAuthRepositoryAdapter.delete(memberId);
        entityManager.flush();

        MemberAuthEntity deletedEntity = entityManager.find(MemberAuthEntity.class, memberId);

        assertThat(deletedEntity).isNull();
    }

    @Test
    @DisplayName("존재하지 않는 회원 인증 정보를 삭제해도 예외가 발생하지 않는다")
    void delete_WithNonExistentId_ShouldNotThrowException() {
        Long nonExistentId = 999L;

        memberAuthRepositoryAdapter.delete(nonExistentId);
    }

    @Test
    @DisplayName("여러 회원 인증 정보가 있을 때 특정 이메일로 조회한다")
    void findByEmail_WithMultipleMembers_ShouldReturnCorrectMember() {
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";
        createAndSaveMemberAuthEntity(1L, email1, "password1", "token1");
        createAndSaveMemberAuthEntity(2L, email2, "password2", "token2");

        Optional<MemberAuth> foundMemberAuth = memberAuthRepositoryAdapter.findByEmail(email1);

        assertAll(
            () -> assertThat(foundMemberAuth).isPresent(),
            () -> assertThat(foundMemberAuth.get().email().getEmailText()).isEqualTo(email1),
            () -> assertThat(foundMemberAuth.get().password()).isEqualTo("password1"),
            () -> assertThat(foundMemberAuth.get().refreshToken()).isEqualTo("token1")
        );
    }

    @Test
    @DisplayName("리프레시 토큰을 null로 업데이트한다")
    void updateRefreshToken_WithNull_ShouldUpdateToNull() {
        MemberAuthEntity savedEntity = createAndSaveMemberAuthEntity(1L, "user@example.com",
            "encodedPassword", "oldRefreshToken");

        memberAuthRepositoryAdapter.updateRefreshToken(savedEntity.getId(), null);
        entityManager.flush();
        entityManager.clear();

        MemberAuthEntity updatedEntity = entityManager.find(MemberAuthEntity.class,
            savedEntity.getId());

        assertThat(updatedEntity.getRefreshToken()).isNull();
    }

    private MemberAuth createMemberAuth(Long id, String email, String password,
        String refreshToken) {
        return MemberAuth.withId(id, email, password, refreshToken);
    }

    private MemberAuthEntity createAndSaveMemberAuthEntity(Long id, String email, String password,
        String refreshToken) {
        MemberAuth memberAuth = createMemberAuth(id, email, password, refreshToken);
        MemberAuthEntity entity = MemberAuthMapper.toEntity(memberAuth);
        return entityManager.persistAndFlush(entity);
    }
}
