package gift.auth.repository;

import gift.auth.domain.MemberAuth;
import gift.auth.jpa.MemberAuthEntity;
import gift.auth.jpa.MemberAuthMapper;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class JpaMemberAuthRepositoryAdapter implements MemberAuthRepository {

    private final JpaMemberAuthRepository jpaMemberAuthRepository;

    public JpaMemberAuthRepositoryAdapter(JpaMemberAuthRepository jpaMemberAuthRepository) {
        this.jpaMemberAuthRepository = jpaMemberAuthRepository;
    }

    @Override
    public Long save(MemberAuth memberAuth) {
        MemberAuthEntity memberAuthEntity = jpaMemberAuthRepository.save(
            MemberAuthMapper.toEntity(memberAuth));
        return memberAuthEntity.getId();
    }

    @Override
    public Optional<MemberAuth> findById(Long memberId) {
        return jpaMemberAuthRepository.findById(memberId).map(MemberAuthMapper::toDomain);
    }

    @Override
    public Optional<MemberAuth> findByEmail(String email) {
        return jpaMemberAuthRepository.findByEmail(email).map(MemberAuthMapper::toDomain);
    }

    @Override
    public void update(Long memberId, MemberAuth updatedMemberAuth) {
        MemberAuthEntity memberAuthEntity = jpaMemberAuthRepository.findById(memberId)
            .orElseThrow(IllegalStateException::new);
        memberAuthEntity.updateFromDomain(updatedMemberAuth);
    }

    @Override
    public void updateRefreshToken(Long memberId, String newRefreshToken) {
        MemberAuthEntity memberAuthEntity = jpaMemberAuthRepository.findById(memberId)
            .orElseThrow(IllegalStateException::new);
        memberAuthEntity.updateRefreshToken(newRefreshToken);
    }

    @Override
    public void delete(Long memberId) {
        jpaMemberAuthRepository.deleteById(memberId);
    }
}
