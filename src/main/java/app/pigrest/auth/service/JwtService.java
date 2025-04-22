package app.pigrest.auth.service;

import app.pigrest.common.TokenType;
import app.pigrest.exception.ExpiredTokenException;
import app.pigrest.exception.InvalidTokenException;
import io.jsonwebtoken.*;
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
        try {
            Claims claims = extractClaims(token);
            if (claims.getExpiration().before(new Date())) {
                throw new ExpiredTokenException(TokenType.ACCESS);
            }
            return claims.getSubject();
        } catch (SignatureException e) {
            throw new InvalidTokenException(TokenType.ACCESS, "Access token has an invalid signature");
        } catch (MalformedJwtException e) {
            throw new InvalidTokenException(TokenType.ACCESS, "Access token is malformed");
        } catch (UnsupportedJwtException e) {
            throw new InvalidTokenException(TokenType.ACCESS, "Access token is not supported");
        }
    }

    public String validateRefreshToken(String token) {
        try {
            Claims claims = extractClaims(token);
            if (claims.getExpiration().before(new Date())) {
                throw new ExpiredTokenException(TokenType.REFRESH);
            }

            String username = redisService.getValue(REFRESH_TOKEN_PREFIX + token);
            if (username == null) {
                throw new InvalidTokenException(TokenType.REFRESH, "Refresh token is invalid or has been revoked");
            }
            if (!username.equals(claims.getSubject())) {
                throw new InvalidTokenException(TokenType.REFRESH, "Refresh token does not match the expected user");
            }
            return username;
        } catch (SignatureException e) {
            throw new InvalidTokenException(TokenType.REFRESH, "Refresh token has an invalid signature");
        } catch (MalformedJwtException e) {
            throw new InvalidTokenException(TokenType.REFRESH, "Refresh token is malformed");
        } catch (UnsupportedJwtException e) {
            throw new InvalidTokenException(TokenType.REFRESH, "Refresh token is not supported");
        }
    }

    public void revokeRefreshToken(String token) {
        redisService.deleteValue(REFRESH_TOKEN_PREFIX + token);
    }
}
