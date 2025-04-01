package app.pigrest.common;

import lombok.Getter;

@Getter
public enum ApiStatusCode {
    // 200: 성공 상태 코드
    OK(200, "OK"),
    CREATED(201, "CREATED"),
    ACCEPTED(202, "ACCEPTED"),
    NO_CONTENT(204, "NO_CONTENT"),

    // 400: 클라이언트 오류
    VALIDATION_ERROR(400, "VALIDATION_ERROR"),
    DUPLICATE_RESOURCE(400, "DUPLICATE_RESOURCE"),

    // 401: 인증 오류
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    TOKEN_EXPIRED(401, "TOKEN_EXPIRED"),

    // 403: 권한 오류
    FORBIDDEN(403, "FORBIDDEN"),

    // 404: 리소스 찾을 수 없음
    USER_NOT_FOUND(404, "USER_NOT_FOUND"),
    BOARD_NOT_FOUND(404, "BOARD_NOT_FOUND"),
    PIN_NOT_FOUND(404, "PIN_NOT_FOUND"),
    RESOURCE_NOT_FOUND(404, "RESOURCE_NOT_FOUND"),

    // 500: 서버 오류
    S3_UPLOAD_ERROR(500, "S3_UPLOAD_ERROR"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR");

    private final int status;
    private final String code;

    ApiStatusCode(int status, String code) {
        this.status = status;
        this.code = code;
    }
}
