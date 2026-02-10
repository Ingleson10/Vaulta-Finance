package br.com.vaultfinance.api.repository;

import br.com.vaultfinance.api.domain.conta.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContaRepository extends JpaRepository<Conta, UUID> {
  List<Conta> findByUsuarioId(UUID usuarioId);
}