package app.pigrest.auth.dto;

import lombok.Getter;

@Getter
public class RegisterRequest {
    private String username;
    private String password;
    private String nickname;
}
