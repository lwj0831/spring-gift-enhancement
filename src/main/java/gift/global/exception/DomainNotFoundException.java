package gift.global.exception;

public class DomainNotFoundException extends BusinessException {

    public DomainNotFoundException(ErrorCode errorCode, Long id) {
        super(errorCode, errorCode.getErrorMessage() + " - id(" + id + ")");
    }

    public DomainNotFoundException(ErrorCode errorCode, String uniqueValue) {
        super(errorCode, errorCode.getErrorMessage() + " - uniqueValue(" + uniqueValue + ")");
    }

}
