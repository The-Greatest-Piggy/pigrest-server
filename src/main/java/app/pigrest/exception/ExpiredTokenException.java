package app.pigrest.exception;

import app.pigrest.common.TokenType;
import lombok.Getter;

@Getter
public class ExpiredTokenException extends CustomJwtException {
    public ExpiredTokenException(TokenType tokenType) {
        super(tokenType, tokenType + " token has expired");
    }
}
