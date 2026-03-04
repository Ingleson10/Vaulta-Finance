package br.com.vaultfinance.api.repository.lancamento;

import br.com.vaultfinance.api.domain.lancamento.Lancamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LancamentoRepository extends JpaRepository<Lancamento, UUID> {

    List<Lancamento> findByUsuarioId(UUID usuarioId);
    Optional<Lancamento> findByIdAndUsuarioId(UUID id, UUID usuarioId);
    Page<Lancamento> findAllByUsuarioId(UUID usuarioId, Pageable pageable);

    Page<Lancamento> findAllByUsuarioIdAndDataOcorrenciaBetween(
        UUID usuarioId, LocalDate inicio, LocalDate fim, Pageable pageable
    );

    // ✅ Query ajustada para ser mais direta
    @Query("SELECT SUM(m.valor) FROM Movimentacao m " +
           "WHERE m.lancamento.usuario.id = :usuarioId " +
           "AND m.lancamento.status = 'CONFIRMADO' " +
           "AND m.natureza = :tipo")
    BigDecimal somarTotalPorTipo(@Param("usuarioId") UUID usuarioId, @Param("tipo") String tipo);
}