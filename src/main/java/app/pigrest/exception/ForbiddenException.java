package app.pigrest.exception;

import app.pigrest.common.ApiStatusCode;
import lombok.Getter;

@Getter
public class ForbiddenException extends CustomException {

    public ForbiddenException(ApiStatusCode statusCode, String message) {
        super(statusCode, message);
    }
}
