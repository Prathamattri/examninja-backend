package in.prathamattri.examninja.exception;

import in.prathamattri.examninja.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice(annotations = RestController.class)
public class HttpExceptionHandler {

    @ExceptionHandler(exception = HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleException(HttpClientErrorException exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getStatusText());
        return ResponseEntity
                .status(exception.getStatusCode())
                .body(errorResponse);
    }
}
