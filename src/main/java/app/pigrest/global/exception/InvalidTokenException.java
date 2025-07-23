package app.pigrest.global.exception;

import app.pigrest.global.common.TokenType;
import lombok.Getter;

@Getter
public class InvalidTokenException extends CustomJwtException {
    public InvalidTokenException(TokenType tokenType, String message) {
        super(tokenType, message);
    }
}
