package app.pigrest.auth.dto.request;

import lombok.Getter;

@Getter
public class RegisterRequest {
    private String id;
    private String password;
    private String passwordConfirm;
    private String username;
}
