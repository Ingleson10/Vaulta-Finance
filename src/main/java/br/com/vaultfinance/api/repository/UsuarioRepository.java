package br.com.vaultfinance.api.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.vaultfinance.api.domain.usuario.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
  Optional<Usuario> findByEmail(String email);
  boolean existsByEmail(String email);
}