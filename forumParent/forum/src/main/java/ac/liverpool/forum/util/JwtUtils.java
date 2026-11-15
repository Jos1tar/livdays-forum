package ac.liverpool.forum.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT helper. The signing key is read from the "jwt.secret" property
 * (environment variable JWT_SECRET, see application.yml).
 */
@Slf4j
@Component
public class JwtUtils {

    private static final long EXPIRE_TIME = 12 * 60 * 60 * 1000L; // 12 hours
    private static volatile SecretKey signKey;

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("jwt.secret (JWT_SECRET) must be at least 32 bytes long");
        }
        signKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public static String generateJwt(Long userId, Map<String, Object> claims) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(signKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims parseJWT(String jwt) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signKey)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.info("Token expired: {}", e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            // bad signature, malformed or unsupported token
            log.info("Invalid token: {}", e.getMessage());
        }
        return null;
    }
}
