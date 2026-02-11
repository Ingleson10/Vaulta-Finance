package br.com.vaultfinance.api.web.dto.lancamento;

import br.com.vaultfinance.api.domain.lancamento.StatusLancamento;
import br.com.vaultfinance.api.domain.lancamento.TipoCategoria;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record LancamentoResponse(
  UUID id,
  UUID usuarioId,
  String descricao,
  TipoCategoria tipo,
  StatusLancamento status,
  LocalDate dataOcorrencia,
  String observacoes,
  List<MovimentacaoResponse> movimentacoes,
  BigDecimal saldoContaAposOperacao
) {}