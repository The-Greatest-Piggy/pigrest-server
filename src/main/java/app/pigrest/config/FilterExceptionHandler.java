package app.pigrest.config;

import app.pigrest.common.ApiResponse;
import app.pigrest.common.ApiStatusCode;
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
    private final ObjectMapper objectMapper;

    public void handle(HttpServletResponse response, Exception e) throws IOException {
        ApiStatusCode apiStatusCode = ApiStatusCode.INTERNAL_SERVER_ERROR;
        String message = e.getMessage();

        if (e instanceof JwtException) {
            apiStatusCode = ApiStatusCode.INVALID_TOKEN;
            message = "Invalid token. Please log in again.";
        } else if (e instanceof AuthenticationException) {
            apiStatusCode = ApiStatusCode.UNAUTHORIZED;
            message = "Authentication failed. Please check your credentials.";
        }

        response.setStatus(apiStatusCode.getStatus());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), ApiResponse.error(apiStatusCode, message));
    }
}
