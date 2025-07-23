package app.pigrest.global.exception;

import app.pigrest.global.common.ApiStatusCode;
import lombok.Getter;

@Getter
public class ForbiddenException extends CustomException {

    public ForbiddenException(ApiStatusCode statusCode, String message) {
        super(statusCode, message);
    }
}
