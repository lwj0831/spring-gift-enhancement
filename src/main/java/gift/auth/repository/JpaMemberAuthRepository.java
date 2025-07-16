package gift.auth.repository;

import gift.auth.jpa.MemberAuthEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaMemberAuthRepository extends JpaRepository<MemberAuthEntity, Long> {
  Optional<MemberAuthEntity> findByEmail(String email);

}
