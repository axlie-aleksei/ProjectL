package org.axlie.projectl.launcher_project;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private static final long TOKEN_EXPIRATION_SHORT = 1; //12 hours
    private static final long TOKEN_EXPIRATION_LONG = 7 * 24 * 60 * 60 * 1000;// 7 days

    public String generateToken(String username, boolean longExpiration) {
        long expiration = longExpiration ? TOKEN_EXPIRATION_LONG : TOKEN_EXPIRATION_SHORT;
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public String validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired");
            return null;
        } catch (JwtException e) {
            System.out.println("Token invalid");
            return null;
        }
    }
}

