package br.com.vaultfinance.api.service;

import br.com.vaultfinance.api.repository.UsuarioRepository;
import br.com.vaultfinance.api.security.UsuarioPrincipal;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UsuarioRepository usuarioRepository;

  public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
    this.usuarioRepository = usuarioRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    var usuario = usuarioRepository.findByEmailIgnoreCase(email)
      .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

    return new UsuarioPrincipal(usuario); // ✅ aqui é Usuario
  }
}
