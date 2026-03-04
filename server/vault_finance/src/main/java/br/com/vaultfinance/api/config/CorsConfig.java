package br.com.vaultfinance.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ Permite o Next.js (em desenvolvimento)
        config.setAllowedOrigins(List.of("http://localhost:3000"));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Liberando headers necessários para o JWT e requisições JSON
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        
        // Expõe o Set-Cookie para que o navegador processe o cookie HttpOnly
        config.setExposedHeaders(List.of("Set-Cookie")); 
        
        // ✅ ESSENCIAL: Permite o envio de cookies entre origens diferentes
        config.setAllowCredentials(true); 
        
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}