package br.com.vaultfinance.api.repository;

import br.com.vaultfinance.api.domain.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {}