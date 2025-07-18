package gift.member.exception;

import gift.global.exception.DomainNotFoundException;

public class MemberNotFoundException extends DomainNotFoundException {

    public MemberNotFoundException(Long id) {
        super(MemberErrorCode.MEMBER_NOT_FOUND, id);
    }

    public MemberNotFoundException(String email) {
        super(MemberErrorCode.MEMBER_NOT_FOUND, email);
    }
}
