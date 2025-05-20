package oneseoktwojo.ohtalkhae.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private int status;      // HTTP 상태 코드 (ex. 200, 400, 500)
    private String message;  // 응답 메시지
    private T data;          // 응답 데이터

    // 요청 성공 시 사용하는 메서드
    public static <T> ApiResponse<T> success(int status, T data) {
        return new ApiResponse<>(status, "Success", data);
    }
    public static <T> ResponseEntity<ApiResponse<?>> success(HttpStatus status, T data) {
        return ResponseEntity.status(status).body(ApiResponse.success(status.value(), data));
    }

    // 요청 실패 시 사용하는 메서드
    public static ApiResponse<?> error(int status, String message) {
        return new ApiResponse<>(status, message, null);
    }
    public static ResponseEntity<ApiResponse<?>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ApiResponse.error(status.value(), message));
    }
    public static <T> ApiResponse<?> error(int status, String message, T data) { return new ApiResponse<>(status, message, data); }
    public static <T> ResponseEntity<ApiResponse<?>> error(HttpStatus status, String message, T data) {
        return ResponseEntity.status(status).body(ApiResponse.error(status.value(), message, data));
    }
}
