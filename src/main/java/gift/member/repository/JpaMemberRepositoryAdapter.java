package gift.member.repository;

import gift.member.domain.Member;
import gift.member.exception.MemberNotFoundException;
import gift.member.jpa.MemberEntity;
import gift.member.jpa.MemberMapper;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class JpaMemberRepositoryAdapter implements MemberRepository{
  private final JpaMemberRepository jpaMemberRepository;

  public JpaMemberRepositoryAdapter(JpaMemberRepository jpaMemberRepository) {
    this.jpaMemberRepository = jpaMemberRepository;
  }

  @Override
  public Long save(Member member) {
    MemberEntity memberEntity = jpaMemberRepository.save(MemberMapper.toEntity(member));
    return memberEntity.getId();
  }

  @Override
  public Optional<Member> findById(Long id) {
    return jpaMemberRepository.findById(id).map(MemberMapper::toDomain);
  }

  @Override
  public void update(Long id, Member updatedMember) {
    MemberEntity memberEntity = jpaMemberRepository.findById(id)
        .orElseThrow(MemberNotFoundException::new);
    memberEntity.updateFromDomain(updatedMember);
  }

  @Override
  public void delete(Long id) {
    jpaMemberRepository.deleteById(id);
  }
}
