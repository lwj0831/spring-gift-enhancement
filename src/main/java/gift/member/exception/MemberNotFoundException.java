package gift.member.exception;

import gift.global.exception.BusinessException;

public class MemberNotFoundException extends BusinessException {

    private final String uniqueValue;

    public MemberNotFoundException(Long id) {
        super(MemberErrorCode.MEMBER_NOT_FOUND);
        this.uniqueValue = id.toString();
    }

    public MemberNotFoundException(String email) {
        super(MemberErrorCode.MEMBER_NOT_FOUND);
        this.uniqueValue = email;
    }

    public String getUniqueValue() {
        return uniqueValue;
    }
}
