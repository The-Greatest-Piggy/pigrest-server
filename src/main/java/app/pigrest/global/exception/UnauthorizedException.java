package app.pigrest.global.exception;

import app.pigrest.global.common.ApiStatusCode;

public class UnauthorizedException extends CustomException {

    public UnauthorizedException(ApiStatusCode statusCode, String message) {
        super(statusCode, message);
    }
}
