package br.com.vaultfinance.api.web.dto.conta;

import java.math.BigDecimal;
import java.util.UUID;

public record ContaResponse(
  UUID id,
  UUID usuarioId,
  String nome,
  String tipo,
  String moeda,
  BigDecimal saldoInicial,
  boolean arquivada
) {}
