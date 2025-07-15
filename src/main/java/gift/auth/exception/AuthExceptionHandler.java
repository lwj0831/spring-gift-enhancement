package gift.auth.exception;

import gift.global.exception.ErrorResponseFactory;
import gift.global.exception.dto.ErrorResponse;
import gift.product.exception.ProductExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(ProductExceptionHandler.class);

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<ErrorResponse> handleInvalidTokenException(
      InvalidTokenException exception) {
    logger.error("Invalid token exception: {}", exception.getMessage());
    return ErrorResponseFactory.createErrorResponse(exception.getErrorCode());
  }

  @ExceptionHandler(ExpiredTokenException.class)
  public ResponseEntity<ErrorResponse> handleExpiredTokenException(
      ExpiredTokenException exception) {
    logger.error("Expired token exception: {}", exception.getMessage());
    return ErrorResponseFactory.createErrorResponse(exception.getErrorCode());
  }

  @ExceptionHandler(DuplicatedEmailException.class)
  public ResponseEntity<ErrorResponse> handleDuplicatedEmailException(
      DuplicatedEmailException exception) {
    logger.error("Duplicated email exception: {}", exception.getMessage());
    return ErrorResponseFactory.createErrorResponse(exception.getErrorCode());
  }

  @ExceptionHandler(PasswordMismatchException.class)
  public ResponseEntity<ErrorResponse> handlePasswordMismatchException(
      PasswordMismatchException exception) {
    logger.error("Password mismatch exception: {}", exception.getMessage());
    return ErrorResponseFactory.createErrorResponse(exception.getErrorCode());
  }

}
