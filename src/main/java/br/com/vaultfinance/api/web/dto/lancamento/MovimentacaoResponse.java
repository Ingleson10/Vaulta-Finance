package br.com.vaultfinance.api.web.dto.lancamento;

import java.math.BigDecimal;
import java.util.UUID;

public record MovimentacaoResponse(
  UUID id,
  UUID contaId,
  UUID categoriaId,
  String natureza,
  BigDecimal valor
) {}
