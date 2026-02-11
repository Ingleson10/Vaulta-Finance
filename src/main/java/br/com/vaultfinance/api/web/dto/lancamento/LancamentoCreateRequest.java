package br.com.vaultfinance.api.web.dto.lancamento;

import br.com.vaultfinance.api.domain.lancamento.TipoCategoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record LancamentoCreateRequest(
  @NotNull UUID usuarioId,
  @NotBlank @Size(max = 200) String descricao,
  @NotNull TipoCategoria tipo,
  @NotNull LocalDate dataOcorrencia,
  @NotNull UUID contaId,
  UUID categoriaId,
  @NotNull @Positive BigDecimal valor,
  String observacoes
) {}