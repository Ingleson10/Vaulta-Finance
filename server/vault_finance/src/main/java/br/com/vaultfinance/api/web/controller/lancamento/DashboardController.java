package br.com.vaultfinance.api.web.controller.lancamento;

import br.com.vaultfinance.api.security.UsuarioPrincipal;
import br.com.vaultfinance.api.service.lancamento.LancamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final LancamentoService lancamentoService;

    public DashboardController(LancamentoService lancamentoService) {
        this.lancamentoService = lancamentoService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getResumo(@AuthenticationPrincipal UsuarioPrincipal user) {
        // O ID do usuário é extraído automaticamente do Token JWT validado
        UUID id = user.getId();
        
        // Chamadas ao Service para buscar os totais processados no Banco
        BigDecimal receitas = lancamentoService.somarTotalPorTipo(id, "CREDITO");
        BigDecimal despesas = lancamentoService.somarTotalPorTipo(id, "DEBITO");
        
        // Proteção contra valores nulos para evitar erros de cálculo no Java
        receitas = (receitas != null) ? receitas : BigDecimal.ZERO;
        despesas = (despesas != null) ? despesas : BigDecimal.ZERO;

        Map<String, Object> resumo = new HashMap<>();
        resumo.put("totalReceitas", receitas);
        resumo.put("totalDespesas", despesas);
        resumo.put("saldoGeral", receitas.subtract(despesas));
        
        return ResponseEntity.ok(resumo);
    }
}