package app.pigrest.exception;

import app.pigrest.common.TokenType;
import lombok.Getter;

@Getter
public class MissingTokenException extends CustomJwtException {
    public MissingTokenException(TokenType tokenType, String message) {
        super(tokenType, message);
    }
}
