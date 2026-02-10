package br.com.vaultfinance.api.web.dto.conta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record ContaCreateRequest(
  @NotNull UUID usuarioId,
  @NotBlank @Size(max = 80) String nome,
  @NotBlank String tipo,
  @Size(min = 3, max = 3) String moeda,
  BigDecimal saldoInicial
) {}