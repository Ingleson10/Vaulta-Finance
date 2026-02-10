package br.com.vaultfinance.api.web.dto.categoria;

import java.util.UUID;

public record CategoriaResponse(
  UUID id,
  UUID usuarioId,
  String nome,
  String tipo,
  String cor,
  String icone
) {}