package oneseoktwojo.ohtalkhae.global.exception;

import lombok.extern.slf4j.Slf4j;
import oneseoktwojo.ohtalkhae.global.dto.ApiResponse;
import oneseoktwojo.ohtalkhae.global.exception.friendexception.DuplicateFriendRequestException;
import oneseoktwojo.ohtalkhae.global.exception.friendexception.FriendRequestNotFoundException;
import oneseoktwojo.ohtalkhae.global.exception.friendexception.ResourceNotFoundException;
import oneseoktwojo.ohtalkhae.global.exception.friendexception.SelfFriendRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
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
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ApiResponse<?> handleIOException(IOException ex) {
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "프로필 사진 업로드에 실패했습니다 : " + ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ApiResponse<?> handleGeneralException(Exception ex) {
        log.debug(ex.getMessage());
        return ApiResponse.error(401, "Unauthorized");
    }

    // 커스텀 친구 예외 핸들러
    @ExceptionHandler(SelfFriendRequestException.class)
    public ApiResponse<?> handleSelfFriend(SelfFriendRequestException ex) {
        return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(DuplicateFriendRequestException.class)
    public ApiResponse<?> handleDuplicateFriend(DuplicateFriendRequestException ex) {
        return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(FriendRequestNotFoundException.class)
    public ApiResponse<?> handleRequestNotFound(FriendRequestNotFoundException ex) {
        return ApiResponse.error(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ApiResponse<?> handleResourceNotFound(ResourceNotFoundException ex) {
        return ApiResponse.error(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }
}