package br.com.vaultfinance.api.repository;

import br.com.vaultfinance.api.domain.movimentacao.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, UUID> {

  List<Movimentacao> findByLancamentoId(UUID lancamentoId);

  @Query("""
    select coalesce(sum(
      case when m.natureza = 'CREDITO' then m.valor else -m.valor end
    ), 0)
    from Movimentacao m
    where m.conta.id = :contaId
  """)
  BigDecimal calcularSaldoAtual(UUID contaId);
}