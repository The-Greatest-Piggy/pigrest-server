package app.pigrest.global.exception;

import app.pigrest.global.common.ApiStatusCode;

public class DuplicateResourceException extends CustomException {
    public DuplicateResourceException(String message) {
        super(ApiStatusCode.DUPLICATE_RESOURCE, message);
    }
}

