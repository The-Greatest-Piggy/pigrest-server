package app.pigrest.global.exception;

import app.pigrest.global.common.TokenType;
import lombok.Getter;

@Getter
public class ExpiredTokenException extends CustomJwtException {
    public ExpiredTokenException(TokenType tokenType) {
        super(tokenType, tokenType + " token has expired");
    }
}
