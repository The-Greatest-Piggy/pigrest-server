package app.pigrest.exception;

import app.pigrest.common.ApiStatusCode;

public class UnauthorizedException extends CustomException {

    public UnauthorizedException(ApiStatusCode statusCode, String message) {
        super(statusCode, message);
    }
}
