package app.pigrest.exception;

import app.pigrest.common.ApiStatusCode;

public class DuplicateResourceException extends CustomException {
    public DuplicateResourceException(String message) {
        super(ApiStatusCode.DUPLICATE_RESOURCE, message);
    }
}

