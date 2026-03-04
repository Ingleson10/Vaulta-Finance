package br.com.vaultfinance.api.web.dto.conta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ContaCreateRequest(
  @NotBlank @Size(max = 80) String nome,
  @NotBlank String tipo,
  @Size(min = 3, max = 3) String moeda,
  BigDecimal saldoInicial
) {}
