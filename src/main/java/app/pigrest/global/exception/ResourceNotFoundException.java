package app.pigrest.global.exception;

import app.pigrest.global.common.ApiStatusCode;

public class ResourceNotFoundException extends CustomException {
    public ResourceNotFoundException(ApiStatusCode statusCode, String message) {
        super(statusCode, message);
    }
}
