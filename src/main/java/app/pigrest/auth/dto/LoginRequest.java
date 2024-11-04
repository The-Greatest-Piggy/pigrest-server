package app.pigrest.auth.dto;

import lombok.Getter;

@Getter
public class LoginRequest {
    // TODO: 유효성 검증
    // TODO: record 변경 여부 확인
    private String username;
    private String password;
}
