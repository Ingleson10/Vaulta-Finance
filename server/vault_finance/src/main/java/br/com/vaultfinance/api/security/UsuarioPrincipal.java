package br.com.vaultfinance.api.security;

import br.com.vaultfinance.api.domain.usuario.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class UsuarioPrincipal implements UserDetails {

  private static final long serialVersionUID = 1L;

  private final UUID id;
  private final String email;
  private final String senhaHash;
  private final boolean ativo;

  public UsuarioPrincipal(Usuario u) {
    this.id = u.getId();
    this.email = u.getEmail();
    this.senhaHash = u.getSenhaHash();
    this.ativo = u.isAtivo();
  }

  public UUID getId() {
    return id;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_USER"));
  }

  @Override
  public String getPassword() {
    return senhaHash;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() { return true; }

  @Override
  public boolean isAccountNonLocked() { return true; }

  @Override
  public boolean isCredentialsNonExpired() { return true; }

  @Override
  public boolean isEnabled() { return ativo; }
}
