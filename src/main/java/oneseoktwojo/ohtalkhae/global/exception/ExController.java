package oneseoktwojo.ohtalkhae.global.exception;

import lombok.extern.slf4j.Slf4j;
import oneseoktwojo.ohtalkhae.global.dto.ApiResponse;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExController {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ApiResponse.error(400, "Invalid Values", errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.debug(ex.getMessage());
        return ApiResponse.error(400, "Invalid Request Body");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ApiResponse<?> handleGeneralException(Exception ex) {
        log.debug(ex.getMessage());
        return ApiResponse.error(401, "Unauthorized");
    }
//    @ExceptionHandler
}
