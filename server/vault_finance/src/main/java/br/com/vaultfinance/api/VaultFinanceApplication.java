package br.com.vaultfinance.api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.vaultfinance.api.repository.usuario.UsuarioRepository;

@SpringBootApplication
public class VaultFinanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VaultFinanceApplication.class, args);
    }

    /**
     * Documentação: Este código roda ao iniciar a aplicação e garante que a senha 
     * do usuário no banco seja compatível com o algoritmo BCrypt do Spring Security.
     */
    @Bean
    CommandLineRunner init(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Buscamos o usuário pelo e-mail que vimos no seu pgAdmin
            repository.findByEmailIgnoreCase("erik@teste.com").ifPresent(usuario -> {
                // Encriptamos a senha "12345" corretamente
                usuario.setSenhaHash(passwordEncoder.encode("12345"));
                repository.save(usuario);
                
                System.out.println("----------------------------------------------");
                System.out.println("SENHA DO ERIK ATUALIZADA COM SUCESSO!");
                System.out.println("----------------------------------------------");
            });
        };
    }
}