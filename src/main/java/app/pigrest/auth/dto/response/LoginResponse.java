package app.pigrest.auth.dto.response;

public record LoginResponse(
        String accessToken,
        String username
) {
    public static LoginResponse of(String accessToken, String username) {
        return new LoginResponse(accessToken, username);
    }
}