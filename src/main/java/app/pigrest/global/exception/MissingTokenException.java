package app.pigrest.global.exception;

import app.pigrest.global.common.TokenType;
import lombok.Getter;

@Getter
public class MissingTokenException extends CustomJwtException {
    public MissingTokenException(TokenType tokenType, String message) {
        super(tokenType, message);
    }
}
