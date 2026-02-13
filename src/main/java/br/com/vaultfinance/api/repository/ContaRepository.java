package br.com.vaultfinance.api.repository;

import br.com.vaultfinance.api.domain.conta.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContaRepository extends JpaRepository<Conta, UUID> {
	
	Page<Conta> findAllByUsuarioId(UUID usuarioId, Pageable pageable);

  // Listagem por usuário (novo padrão)
  List<Conta> findAllByUsuarioId(UUID usuarioId);

  // Compatibilidade (se você ainda usa em outras classes)
  List<Conta> findByUsuarioId(UUID usuarioId);

  // Busca segura (ownership)
  Optional<Conta> findByIdAndUsuarioId(UUID id, UUID usuarioId);

  // Segurança ao deletar/validar acesso
  boolean existsByIdAndUsuarioId(UUID id, UUID usuarioId);
}
