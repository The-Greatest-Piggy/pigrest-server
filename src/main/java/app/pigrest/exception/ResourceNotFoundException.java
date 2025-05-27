package app.pigrest.exception;

import app.pigrest.common.ApiStatusCode;

public class ResourceNotFoundException extends CustomException {
    public ResourceNotFoundException(ApiStatusCode statusCode, String message) {
        super(statusCode, message);
    }
}
