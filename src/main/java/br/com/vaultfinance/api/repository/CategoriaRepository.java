package br.com.vaultfinance.api.repository;

import br.com.vaultfinance.api.domain.categoria.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

  List<Categoria> findByUsuarioId(UUID usuarioId);

  boolean existsByUsuarioIdAndNomeIgnoreCase(UUID usuarioId, String nome);
}