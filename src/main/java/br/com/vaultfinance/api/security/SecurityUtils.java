package br.com.vaultfinance.api.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class SecurityUtils {

  private SecurityUtils() {}

  public static UUID getUsuarioId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getPrincipal() == null) {
      throw new IllegalStateException("Usuário não autenticado");
    }

    Object principal = auth.getPrincipal();
    if (principal instanceof UsuarioPrincipal up) {
      return up.getId();
    }

    throw new IllegalStateException("Principal inválido");
  }
}