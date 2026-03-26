package in.prathamattri.examninja.exception;

import in.prathamattri.examninja.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice(annotations = RestController.class)
public class HttpExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, List<String>> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            if (errors.containsKey(error.getField())) {
                errors.get(error.getField()).add(error.getDefaultMessage());
            } else {
                List<String> errorList = new ArrayList<>();
                errorList.add(error.getDefaultMessage());
                errors.put(error.getField(), errorList);
            }
            System.out.println(error.getDefaultMessage());
        });

        ErrorResponse errorResponse = new ErrorResponse("Bad request body", errors);

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(exception = HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleException(HttpClientErrorException exception) {
        String exceptionResponseBody = exception.getResponseBodyAsString();
        ErrorResponse errorResponse = new ErrorResponse(
                exception.getStatusText(),
                Map.of("error", List.of(exceptionResponseBody))
        );
        return ResponseEntity
                .status(exception.getStatusCode())
                .body(errorResponse);
    }
}
