package app.pigrest.common;

import lombok.Getter;

import java.time.Instant;

@Getter
public class ApiResponse<T> {
    private final int status;
    private final String message;
    private final String error;
    private final String timestamp;
    private final T data;

    private ApiResponse(int status, String message, String error, T data) {
        this.status = status;
        this.message = message;
        this.error = error;
        this.timestamp = Instant.now().toString();
        this.data = data;
    }

    public static <R> ApiResponse<R> success(ApiStatusCode statusCode, String message, R data) {
        return new ApiResponse<>(statusCode.getStatus(), message, null, data);
    }

    public static <R> ApiResponse<R> error(ApiStatusCode statusCode, String message) {
        return new ApiResponse<>(statusCode.getStatus(), message, statusCode.getCode(), null);
    }

    public static <R> ApiResponse<R> noContent() {
        return success(ApiStatusCode.NO_CONTENT, null, null);
    }
}
