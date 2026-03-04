package br.com.vaultfinance.api.web.controller.auth;

import br.com.vaultfinance.api.domain.exception.BusinessException;
import br.com.vaultfinance.api.domain.usuario.Usuario;
import br.com.vaultfinance.api.repository.usuario.UsuarioRepository;
import br.com.vaultfinance.api.security.JwtService;
import br.com.vaultfinance.api.security.UsuarioPrincipal;
import br.com.vaultfinance.api.web.dto.auth.AuthResponse;
import br.com.vaultfinance.api.web.dto.auth.LoginRequest;
import br.com.vaultfinance.api.web.dto.auth.RegisterRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String COOKIE_NAME = "VF_TOKEN";
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
    public AuthResponse register(@RequestBody @Valid RegisterRequest req, HttpServletResponse response) {
        if (usuarioRepository.existsByEmailIgnoreCase(req.email())) {
            throw new BusinessException("E-mail já cadastrado");
        }

        Usuario u = new Usuario();
        u.setNome(req.nome());
        u.setEmail(req.email());
        u.setSenhaHash(passwordEncoder.encode(req.senha()));
        u.setAtivo(true);

        Usuario salvo = usuarioRepository.save(u);
        String token = jwtService.generateToken(salvo.getEmail());
        setAuthCookie(response, token);

        // ✅ Agora retorna o token no JSON também
        return new AuthResponse(token, salvo.getId(), salvo.getNome(), salvo.getEmail());
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest req, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.senha())
            );
        } catch (AuthenticationException ex) {
            throw ex;
        }

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(req.email())
            .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        String token = jwtService.generateToken(usuario.getEmail());
        setAuthCookie(response, token);

        // ✅ Token preenchido na resposta
        return new AuthResponse(token, usuario.getId(), usuario.getNome(), usuario.getEmail());
    }

    @GetMapping("/me")
    public AuthResponse me(@AuthenticationPrincipal UsuarioPrincipal principal) {
        Usuario usuario = usuarioRepository.findById(principal.getId())
            .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Em /me, o token pode ser null se o front já o tiver, ou você pode gerar um novo (refresh)
        return new AuthResponse(null, usuario.getId(), usuario.getNome(), usuario.getEmail());
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, "")
            .httpOnly(true)
            .secure(false)
            .sameSite("Lax")
            .path("/")
            .maxAge(Duration.ZERO)
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void setAuthCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, token)
            .httpOnly(true)
            .secure(false) 
            .sameSite("Lax")
            .path("/")
            .maxAge(Duration.ofHours(24))
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}