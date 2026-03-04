package br.com.vaultfinance.api.security;

import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

  public static UUID getUsuarioId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth == null || !auth.isAuthenticated()) {
      throw new IllegalStateException("Usuário não autenticado");
    }

    Object principal = auth.getPrincipal();

    if (principal instanceof UsuarioPrincipal up) {
      return up.getId();
    }

    throw new IllegalStateException("Principal inválido: " + principal.getClass().getName());
  }
}