package app.pigrest.config;

import app.pigrest.auth.service.JwtService;
import app.pigrest.common.TokenType;
import app.pigrest.exception.MissingTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final List<String> whiteListUrls = List.of(
            "/v3/api-docs/",
            "/openapi3.yaml",
            "/swagger-ui",
            "/auth"
    );

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    private boolean isWhitelistedUrl(String servletPath) {
        return whiteListUrls.stream().anyMatch(servletPath::contains);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isWhitelistedUrl(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new MissingTokenException(TokenType.ACCESS, "Access token is missing from Authorization Header");
        }

        String token = authHeader.substring(7);
        String username = jwtService.validateAccessToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}
