package br.com.vaultfinance.api.web.controller;

import br.com.vaultfinance.api.service.LancamentoService;
import br.com.vaultfinance.api.web.dto.lancamento.LancamentoCreateRequest;
import br.com.vaultfinance.api.web.dto.lancamento.LancamentoResponse;
import br.com.vaultfinance.api.web.dto.lancamento.TransferenciaCreateRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {

  private final LancamentoService lancamentoService;

  public LancamentoController(LancamentoService lancamentoService) {
    this.lancamentoService = lancamentoService;
  }

  // Receita/Despesa (usuário vem do JWT)
  @PostMapping
  public LancamentoResponse criarReceitaOuDespesa(@RequestBody @Valid LancamentoCreateRequest req) {
    return lancamentoService.criarReceitaOuDespesa(req);
  }

  // Transferência (usuário vem do JWT)
  @PostMapping("/transferencias")
  public LancamentoResponse criarTransferencia(@RequestBody @Valid TransferenciaCreateRequest req) {
    return lancamentoService.criarTransferencia(req);
  }

  // Listar lançamentos do usuário autenticado
  @GetMapping
  public List<LancamentoResponse> listarMeusLancamentos() {
    return lancamentoService.listarMeusLancamentos();
  }
}