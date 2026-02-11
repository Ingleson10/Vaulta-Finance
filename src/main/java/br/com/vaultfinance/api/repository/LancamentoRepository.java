package br.com.vaultfinance.api.repository;

import br.com.vaultfinance.api.domain.lancamento.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LancamentoRepository extends JpaRepository<Lancamento, UUID> {
  List<Lancamento> findByUsuarioId(UUID usuarioId);
}