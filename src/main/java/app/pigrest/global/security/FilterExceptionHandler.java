package app.pigrest.global.security;

import app.pigrest.global.common.ApiResponse;
import app.pigrest.global.common.ApiStatusCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FilterExceptionHandler {
    private record ErrorInfo(ApiStatusCode statusCode, String message) { }

    private final ObjectMapper objectMapper;

    public void handle(HttpServletResponse response, Exception e) throws IOException {
        ErrorInfo error = getErrorInfo(e);

        response.setStatus(error.statusCode().getStatus());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), ApiResponse.error(error.statusCode(), error.message()));
    }

    private ErrorInfo getErrorInfo(Exception e) {
        if (e instanceof JwtException) {
            return new ErrorInfo(ApiStatusCode.INVALID_TOKEN, "Invalid token. Please log in again.");
        } else if (e instanceof AuthenticationException) {
            return new ErrorInfo(ApiStatusCode.UNAUTHORIZED, "Authentication failed. Please check your credentials.");
        }
        return new ErrorInfo(ApiStatusCode.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }
}
