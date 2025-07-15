package gift.auth.repository;

import gift.auth.domain.MemberAuth;
import java.util.Optional;

public interface MemberAuthRepository {

  Long save(MemberAuth memberAuth);

  Optional<MemberAuth> findById(Long memberId);

  Optional<MemberAuth> findByEmail(String email);

  void update(Long memberId, MemberAuth updatedMemberAuth);

  void updateRefreshToken(Long memberId, String newRefreshToken);

  void delete(Long memberId);

}
