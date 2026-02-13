package br.com.vaultfinance.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

  // ⚠️ Ideal: colocar no application.properties e ler via @Value
  private static final String SECRET = "mude_essa_chave_para_uma_maior_e_bem_segura_com_32+chars";
  private static final long EXPIRATION_MS = 1000L * 60 * 60 * 24; // 24h

  private SecretKey key() {
    return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(String email) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + EXPIRATION_MS);

    return Jwts.builder()
      .subject(email)
      .issuedAt(now)
      .expiration(exp)
      .signWith(key())
      .compact();
  }

  public boolean isValid(String token) {
    try {
      extractAllClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public String extractEmail(String token) {
    return extractAllClaims(token).getSubject();
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
      .verifyWith(key())
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }
}
