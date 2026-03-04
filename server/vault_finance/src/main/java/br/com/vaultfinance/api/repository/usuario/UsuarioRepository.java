package br.com.vaultfinance.api.repository.usuario;

import br.com.vaultfinance.api.domain.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
  boolean existsByEmailIgnoreCase(String email);
  Optional<Usuario> findByEmailIgnoreCase(String email);
}
