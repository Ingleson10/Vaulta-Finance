package br.com.vaultfinance.api.web.controller;

import br.com.vaultfinance.api.web.dto.auth.AuthResponse;
import br.com.vaultfinance.api.domain.usuario.Usuario;
import br.com.vaultfinance.api.repository.UsuarioRepository;
import br.com.vaultfinance.api.security.JwtService;
import br.com.vaultfinance.api.security.UsuarioPrincipal;
import br.com.vaultfinance.api.web.dto.auth.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UsuarioRepository usuarioRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public AuthController(
    UsuarioRepository usuarioRepository,
    PasswordEncoder passwordEncoder,
    AuthenticationManager authenticationManager,
    JwtService jwtService
  ) {
    this.usuarioRepository = usuarioRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public void register(@Valid @RequestBody RegisterRequest req) {
    if (usuarioRepository.existsByEmail(req.email())) {
      throw new IllegalArgumentException("E-mail já cadastrado");
    }

    var u = new Usuario();
    u.setNome(req.nome());
    u.setEmail(req.email());
    u.setSenhaHash(passwordEncoder.encode(req.senha()));
    u.setAtivo(true);

    usuarioRepository.save(u);
  }

  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest req) {
    var auth = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(req.email(), req.senha())
    );

    var principal = (UsuarioPrincipal) auth.getPrincipal();
    String token = jwtService.generateToken(principal.getId(), principal.getUsername());

    return new AuthResponse(token);
  }
}