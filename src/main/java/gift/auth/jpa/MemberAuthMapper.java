package gift.auth.jpa;

import gift.auth.domain.MemberAuth;

public class MemberAuthMapper {

  public static MemberAuth toDomain(MemberAuthEntity entity){
    return MemberAuth.withId(entity.getId(),entity.getEmail(), entity.getPassword(),
        entity.getRefreshToken());
  }

  public static MemberAuthEntity toEntity(MemberAuth domain){
    return new MemberAuthEntity(domain.memberId(),domain.email().getEmailText(), domain.password(),
        domain.refreshToken());
  }

}
