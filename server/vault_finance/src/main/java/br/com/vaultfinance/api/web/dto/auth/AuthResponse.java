package br.com.vaultfinance.api.web.dto.auth;

import java.util.UUID;

public record AuthResponse(
  String token,
  UUID usuarioId,
  String email,
  String nome
) {}
