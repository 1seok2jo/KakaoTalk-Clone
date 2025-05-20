package oneseoktwojo.ohtalkhae.global.exception;

import lombok.extern.slf4j.Slf4j;
import oneseoktwojo.ohtalkhae.global.dto.ApiResponse;
import oneseoktwojo.ohtalkhae.global.exception.friendexception.DuplicateFriendRequestException;
import oneseoktwojo.ohtalkhae.global.exception.friendexception.FriendRequestNotFoundException;
import oneseoktwojo.ohtalkhae.global.exception.friendexception.ResourceNotFoundException;
import oneseoktwojo.ohtalkhae.global.exception.friendexception.SelfFriendRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class ExController {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid Values", errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.debug(ex.getMessage());
        return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid Request Body");
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ApiResponse.error(HttpStatus.BAD_REQUEST, "Invalid Request");
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiResponse<?>> handleIOException(IOException ex) {
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "프로필 사진 업로드에 실패했습니다 : " + ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleGeneralException(Exception ex) {
        log.debug(ex.getMessage());
        return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }

    // 커스텀 친구 예외 핸들러
    @ExceptionHandler(SelfFriendRequestException.class)
    public ResponseEntity<ApiResponse<?>> handleSelfFriend(SelfFriendRequestException ex) {
        ApiResponse<?> body = ApiResponse.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ExceptionHandler(DuplicateFriendRequestException.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicateFriend(DuplicateFriendRequestException ex) {
        ApiResponse<?> body = ApiResponse.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ExceptionHandler(FriendRequestNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleRequestNotFound(FriendRequestNotFoundException ex) {
        ApiResponse<?> body = ApiResponse.error(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(body);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiResponse<?> body = ApiResponse.error(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(body);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ApiResponse<?> handleNoSuchElementException(NoSuchElementException ex) {
        return ApiResponse.error(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException.class)
    public ApiResponse<?> handleFileSizeLimitExceededException(org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException ex){
        return ApiResponse.error(HttpStatus.PAYLOAD_TOO_LARGE.value(), "업로드 파일 크기가 제한을 초과했습니다.");
    }
//    @ExceptionHandler

}