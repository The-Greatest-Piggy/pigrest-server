package app.pigrest.exception;

import app.pigrest.common.ApiStatusCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ApiStatusCode statusCode;

    // 생성자: ApiStatusCode와 예외 메시지를 받아서 초기화합니다.
    public CustomException(ApiStatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}
