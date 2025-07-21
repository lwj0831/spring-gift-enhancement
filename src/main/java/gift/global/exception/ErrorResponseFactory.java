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
        String message) {
        return ResponseEntity
            .status(errorCode.getStatus())
            .body(ErrorResponse.from(errorCode, message));
    }

    public static ResponseEntity<ErrorResponse> createErrorResponse(ErrorCode errorCode,
        Map<String, Object> additionalInfo) {
        return ResponseEntity
            .status(errorCode.getStatus())
            .body(ErrorResponse.from(errorCode, additionalInfo));
    }

}
