package br.com.vaultfinance.api.web.dto.lancamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransferenciaCreateRequest(
  @NotNull UUID usuarioId,
  @NotBlank @Size(max = 200) String descricao,
  @NotNull LocalDate dataOcorrencia,
  @NotNull UUID contaOrigemId,
  @NotNull UUID contaDestinoId,
  @NotNull @Positive BigDecimal valor,
  String observacoes
) {}
