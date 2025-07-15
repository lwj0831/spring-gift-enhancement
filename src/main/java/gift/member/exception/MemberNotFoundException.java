package gift.member.exception;

public class MemberNotFoundException extends RuntimeException {

  private final MemberErrorCode errorCode;

  public MemberNotFoundException() {
    super(MemberErrorCode.MEMBER_NOT_FOUND.getErrorMessage());
    this.errorCode = MemberErrorCode.MEMBER_NOT_FOUND;
  }

  public MemberNotFoundException(String message) {
    super(message);
    this.errorCode = MemberErrorCode.MEMBER_NOT_FOUND;
  }

  public MemberErrorCode getErrorCode() {
    return errorCode;
  }
}
