package gift.member.repository;

import gift.member.domain.Member;
import java.util.Optional;

public interface MemberRepository {

  Long save(Member member);

  Optional<Member> findById(Long id);

  void update(Long id, Member updatedMember);

  void delete(Long id);


}
