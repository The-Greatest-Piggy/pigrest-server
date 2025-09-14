package app.pigrest.global.exception;

import app.pigrest.global.common.ApiStatusCode;

public class ConflictException extends CustomException {

    public ConflictException(String message) {
        super(ApiStatusCode.CONFLICT, message);
    }
}
