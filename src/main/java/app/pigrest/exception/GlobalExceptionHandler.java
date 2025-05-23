package app.pigrest.exception;

import app.pigrest.common.ApiResponse;
import app.pigrest.common.ApiStatusCode;
import app.pigrest.common.TokenType;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // CustomException이 발생했을 때 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException ex) {
        // 예외 객체에 포함된 ApiStatusCode와 메시지를 이용해 에러 응답 생성
        return ResponseEntity.ok(ApiResponse.error(ex.getStatusCode(), ex.getMessage()));
    }

    @ExceptionHandler({ InvalidTokenException.class, ExpiredTokenException.class })
    public ResponseEntity<ApiResponse<Object>> handleTokenException(CustomJwtException ex) {
        if (ex.getTokenType().equals(TokenType.REFRESH)) {
            ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                    .path("/")
                    .maxAge(0)
                    .httpOnly(true)
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                    .body(ApiResponse.error(ApiStatusCode.INVALID_TOKEN, ex.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ApiStatusCode.INVALID_TOKEN, ex.getMessage()));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Object>> handleJwtException(JwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ApiStatusCode.INVALID_TOKEN, ex.getMessage()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResourceException(DuplicateResourceException ex) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ex.getStatusCode(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> "[" + fieldError.getField() + "] " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ApiStatusCode.VALIDATION_ERROR, message));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ApiStatusCode.UNAUTHORIZED, ex.getMessage()));
    }

    // 그 외 모든 예외에 대해 처리 (예: NullPointerException, 기타 예상치 못한 예외)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
        // INTERNAL_SERVER_ERROR 상태코드와 예외 메시지를 이용해 에러 응답 생성
        return ResponseEntity.internalServerError()
                .body(ApiResponse.error(ApiStatusCode.INTERNAL_SERVER_ERROR, ex.getMessage()));
    }
}
