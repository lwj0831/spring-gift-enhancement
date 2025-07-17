package gift.member.jpa;

import gift.member.domain.Member;

public class MemberMapper {

    public static Member toDomain(MemberEntity entity) {
        return Member.withId(entity.getId(), entity.getName());
    }

    public static MemberEntity toEntity(Member entity) {
        return new MemberEntity(entity.id(), entity.name());
    }

}
