package app.pigrest.exception;

import app.pigrest.common.TokenType;
import lombok.Getter;

@Getter
public class InvalidTokenException extends CustomJwtException {
    public InvalidTokenException(TokenType tokenType, String message) {
        super(tokenType, message);
    }
}
