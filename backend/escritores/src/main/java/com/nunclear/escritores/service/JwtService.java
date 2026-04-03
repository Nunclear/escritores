package com.nunclear.escritores.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.expiration}")
    private long jwtExpiration;

    private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000; // 7 days
    private static final long PASSWORD_RESET_EXPIRATION = 60 * 60 * 1000; // 1 hour
    private static final long EMAIL_VERIFICATION_EXPIRATION = 24 * 60 * 60 * 1000; // 24 hours

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Generar token de acceso JWT
     */
    public String generateToken(Integer userId, String email, String role) {
        Instant now = Instant.now();
        Instant expiryDate = now.plusMillis(jwtExpiration);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("role", role)
                .claim("type", "ACCESS")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Generar refresh token
     */
    public String generateRefreshToken(Integer userId) {
        Instant now = Instant.now();
        Instant expiryDate = now.plusMillis(REFRESH_TOKEN_EXPIRATION);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("type", "REFRESH")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Generar token de recuperación de contraseña
     */
    public String generatePasswordResetToken(Integer userId) {
        Instant now = Instant.now();
        Instant expiryDate = now.plusMillis(PASSWORD_RESET_EXPIRATION);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("type", "PASSWORD_RESET")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Generar token de verificación de email
     */
    public String generateEmailVerificationToken(Integer userId) {
        Instant now = Instant.now();
        Instant expiryDate = now.plusMillis(EMAIL_VERIFICATION_EXPIRATION);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("type", "EMAIL_VERIFICATION")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validar token de acceso
     */
    public Integer validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String type = claims.get("type", String.class);
            if (!type.equals("ACCESS")) {
                throw new JwtException("Token type inválido");
            }

            return Integer.parseInt(claims.getSubject());
        } catch (JwtException e) {
            throw new RuntimeException("Token JWT inválido: " + e.getMessage());
        }
    }

    /**
     * Validar refresh token
     */
    public Integer validateRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String type = claims.get("type", String.class);
            if (!type.equals("REFRESH")) {
                throw new JwtException("Token type inválido");
            }

            return Integer.parseInt(claims.getSubject());
        } catch (JwtException e) {
            throw new RuntimeException("Refresh token inválido: " + e.getMessage());
        }
    }

    /**
     * Validar token de recuperación de contraseña
     */
    public Integer validatePasswordResetToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String type = claims.get("type", String.class);
            if (!type.equals("PASSWORD_RESET")) {
                throw new JwtException("Token type inválido");
            }

            return Integer.parseInt(claims.getSubject());
        } catch (JwtException e) {
            throw new RuntimeException("Password reset token inválido: " + e.getMessage());
        }
    }

    /**
     * Validar token de verificación de email
     */
    public Integer validateEmailVerificationToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String type = claims.get("type", String.class);
            if (!type.equals("EMAIL_VERIFICATION")) {
                throw new JwtException("Token type inválido");
            }

            return Integer.parseInt(claims.getSubject());
        } catch (JwtException e) {
            throw new RuntimeException("Email verification token inválido: " + e.getMessage());
        }
    }

    /**
     * Extraer userId de token
     */
    public Integer extractUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Integer.parseInt(claims.getSubject());
        } catch (JwtException e) {
            return null;
        }
    }

    /**
     * Extraer email de token
     */
    public String extractEmail(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("email", String.class);
        } catch (JwtException e) {
            return null;
        }
    }

    /**
     * Extraer role de token
     */
    public String extractRole(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("role", String.class);
        } catch (JwtException e) {
            return null;
        }
    }

    /**
     * Verificar si token es válido
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
