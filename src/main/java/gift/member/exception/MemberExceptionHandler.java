package gift.member.exception;

import gift.global.exception.ErrorResponseFactory;
import gift.global.exception.dto.ErrorResponse;
import gift.product.exception.ProductExceptionHandler;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MemberExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProductExceptionHandler.class);

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMemberNotFoundException(
        MemberNotFoundException exception) {
        logger.error("Member not found: {}", exception.getMessage(), exception);

        Map<String, Object> additionalInfo = Map.of("id, email", exception.getUniqueValue());

        return ErrorResponseFactory.createErrorResponse(exception.getErrorCode(), additionalInfo);
    }

}
