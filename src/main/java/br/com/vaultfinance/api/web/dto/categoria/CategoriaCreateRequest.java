package br.com.vaultfinance.api.web.dto.categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CategoriaCreateRequest(
  @NotNull UUID usuarioId,
  @NotBlank @Size(max = 60) String nome,
  @NotBlank String tipo,          // "RECEITA" ou "DESPESA"
  @Size(max = 12) String cor,
  @Size(max = 40) String icone
) {}