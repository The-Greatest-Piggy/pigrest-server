package app.pigrest.auth.dto.response;

public record CheckUsernameResponse(
        boolean isAvailable
) {
    public static CheckUsernameResponse of(boolean isAvailable) {
        return new CheckUsernameResponse(isAvailable);
    }
}
