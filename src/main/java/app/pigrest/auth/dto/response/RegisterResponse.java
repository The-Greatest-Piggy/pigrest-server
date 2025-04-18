package app.pigrest.auth.dto.response;

import app.pigrest.auth.model.Auth;

public record RegisterResponse(
        String username
) {
    public static RegisterResponse from(Auth auth) {
        return new RegisterResponse(auth.getUsername());
    }
}