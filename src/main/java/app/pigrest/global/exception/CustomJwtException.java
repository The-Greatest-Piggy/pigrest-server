package app.pigrest.global.exception;

import app.pigrest.global.common.TokenType;
import io.jsonwebtoken.JwtException;
import lombok.Getter;

@Getter
public class CustomJwtException extends JwtException {
    private final TokenType tokenType;

    public CustomJwtException(TokenType tokenType, String message) {
        super(message);
        this.tokenType = tokenType;
    }
}
