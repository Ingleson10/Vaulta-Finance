package br.com.vaultfinance.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

  private final SecretKey key;
  private final long expirationMinutes;

  public JwtService(
    @Value("${security.jwt.secret}") String base64Secret,
    @Value("${security.jwt.expiration-minutes}") long expirationMinutes
  ) {
    this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
    this.expirationMinutes = expirationMinutes;
  }

  public String generateToken(UUID userId, String email) {
    Instant now = Instant.now();
    Instant exp = now.plus(expirationMinutes, ChronoUnit.MINUTES);

    return Jwts.builder()
      .subject(email)
      .claim("uid", userId.toString())
      .issuedAt(Date.from(now))
      .expiration(Date.from(exp))
      .signWith(key)
      .compact();
  }

  public String extractEmail(String token) {
    return getClaims(token).getSubject();
  }

  public UUID extractUserId(String token) {
    String uid = getClaims(token).get("uid", String.class);
    return UUID.fromString(uid);
  }

  public boolean isValid(String token) {
    try {
      getClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private Claims getClaims(String token) {
    return Jwts.parser()
      .verifyWith(key)
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }
}
