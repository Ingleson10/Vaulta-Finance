package br.com.vaultfinance.api.repository;

import br.com.vaultfinance.api.domain.lancamento.Lancamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LancamentoRepository extends JpaRepository<Lancamento, UUID> {

  // compatibilidade (se você já usa em outros lugares)
  List<Lancamento> findByUsuarioId(UUID usuarioId);

  Optional<Lancamento> findByIdAndUsuarioId(UUID id, UUID usuarioId);

  // ✅ paginação
  Page<Lancamento> findAllByUsuarioId(UUID usuarioId, Pageable pageable);

  // ✅ filtro por período + paginação
  Page<Lancamento> findAllByUsuarioIdAndDataOcorrenciaBetween(
    UUID usuarioId,
    LocalDate inicio,
    LocalDate fim,
    Pageable pageable
  );
}
