package app.pigrest.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;
    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    private final RedisService redisService;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        String refreshToken = Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();

        redisService.setValue(REFRESH_TOKEN_PREFIX + refreshToken,
                userDetails.getUsername(), refreshTokenExpiration, TimeUnit.MILLISECONDS);
        return refreshToken;
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String validateAccessToken(String token) {
        Claims claims = extractClaims(token);
        if (claims.getExpiration().before(new Date())) {
            throw new JwtException("Expired access token");
        }
        return claims.getSubject();
    }

    public String validateRefreshToken(String token) {
        try {
            Claims claims = extractClaims(token);
            if (claims.getExpiration().before(new Date())) {
                throw new JwtException("Expired refresh token");
            }

            String username = redisService.getValue(REFRESH_TOKEN_PREFIX + token);
            if (username == null) {
                throw new JwtException("Invalid or revoked refresh token");
            }
            if (!username.equals(claims.getSubject())) {
                throw new JwtException("Refresh token mismatch");
            }
            return username;
        } catch (SignatureException e) {
            throw new JwtException("Invalid refresh token signature", e);
        } catch (MalformedJwtException e) {
            throw new JwtException("Malformed refresh token", e);
        }
    }
}
