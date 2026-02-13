package br.com.vaultfinance.api.web.controller;

import br.com.vaultfinance.api.domain.usuario.Usuario;
import br.com.vaultfinance.api.repository.UsuarioRepository;
import br.com.vaultfinance.api.security.JwtService;
import br.com.vaultfinance.api.web.dto.auth.AuthResponse;
import br.com.vaultfinance.api.web.dto.auth.LoginRequest;
import br.com.vaultfinance.api.web.dto.auth.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final UsuarioRepository usuarioRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthController(
    AuthenticationManager authenticationManager,
    UsuarioRepository usuarioRepository,
    PasswordEncoder passwordEncoder,
    JwtService jwtService
  ) {
    this.authenticationManager = authenticationManager;
    this.usuarioRepository = usuarioRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  @PostMapping("/register")
  public AuthResponse register(@RequestBody @Valid RegisterRequest req) {

    if (usuarioRepository.existsByEmailIgnoreCase(req.email())) {
      throw new IllegalArgumentException("E-mail já cadastrado");
    }

    Usuario u = new Usuario();
    u.setNome(req.nome());
    u.setEmail(req.email());
    u.setSenhaHash(passwordEncoder.encode(req.senha()));
    u.setAtivo(true);

    Usuario salvo = usuarioRepository.save(u);

    String token = jwtService.generateToken(salvo.getEmail());

    return new AuthResponse(token, salvo.getId(), salvo.getEmail(), salvo.getNome());
  }

  @PostMapping("/login")
  public AuthResponse login(@RequestBody @Valid LoginRequest req) {

    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(req.email(), req.senha())
    );

    Usuario usuario = usuarioRepository.findByEmailIgnoreCase(req.email())
      .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

    String token = jwtService.generateToken(usuario.getEmail());

    return new AuthResponse(token, usuario.getId(), usuario.getEmail(), usuario.getNome());
  }
}
