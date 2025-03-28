package app.pigrest.auth.dto.response;

import app.pigrest.member.model.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RegisterResponse {
    private UUID userId;
    private String username;

    public static RegisterResponse from(Member member) {
        return new RegisterResponse(member.getId(), member.getUsername());
    }
}
