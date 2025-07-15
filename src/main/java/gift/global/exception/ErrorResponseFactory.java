package gift.global.exception;

import gift.global.exception.dto.ErrorResponse;
import java.util.Map;
import org.springframework.http.ResponseEntity;

public class ErrorResponseFactory {

  public static ResponseEntity<ErrorResponse> createErrorResponse(ErrorCode errorCode) {
    return ResponseEntity
        .status(errorCode.getStatus())
        .body(ErrorResponse.from(errorCode));
  }

  public static ResponseEntity<ErrorResponse> createErrorResponse(ErrorCode errorCode,
      Exception exception) {
    return ResponseEntity
        .status(errorCode.getStatus())
        .body(ErrorResponse.from(errorCode, exception.getMessage()));
  }

  public static ResponseEntity<ErrorResponse> createErrorResponse(GlobalErrorCode errorCode,
      Exception exception, Map<String, Object> additionalInfo) {
    return ResponseEntity
        .status(errorCode.getStatus())
        .body(ErrorResponse.from(errorCode, additionalInfo));
  }

}
