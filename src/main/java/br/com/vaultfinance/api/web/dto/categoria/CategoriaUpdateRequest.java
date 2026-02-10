package br.com.vaultfinance.api.web.dto.categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaUpdateRequest(
  @NotBlank @Size(max = 60) String nome,
  @NotBlank String tipo,          // "RECEITA" ou "DESPESA"
  @Size(max = 12) String cor,
  @Size(max = 40) String icone
) {}