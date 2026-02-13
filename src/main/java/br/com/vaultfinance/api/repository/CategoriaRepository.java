package br.com.vaultfinance.api.repository;

import br.com.vaultfinance.api.domain.categoria.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

	Page<Categoria> findAllByUsuarioId(UUID usuarioId, Pageable pageable);
	
  // Listagem por usuário (novo padrão)
  List<Categoria> findAllByUsuarioId(UUID usuarioId);

  // Compatibilidade (se você ainda usa em outras classes)
  List<Categoria> findByUsuarioId(UUID usuarioId);

  // Busca segura (ownership)
  Optional<Categoria> findByIdAndUsuarioId(UUID id, UUID usuarioId);

  // Duplicidade de nome por usuário
  boolean existsByUsuarioIdAndNomeIgnoreCase(UUID usuarioId, String nome);

  // Segurança ao deletar/validar acesso
  boolean existsByIdAndUsuarioId(UUID id, UUID usuarioId);
}
