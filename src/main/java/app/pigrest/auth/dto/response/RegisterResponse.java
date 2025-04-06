package app.pigrest.auth.dto.response;

import app.pigrest.member.model.Member;

import java.util.UUID;

public record RegisterResponse(
        String username
) {
    public static RegisterResponse from(Member member) {
        return new RegisterResponse(member.getNickname());
    }
}