package br.com.vaultfinance.api.web.controller;

import br.com.vaultfinance.api.service.ContaService;
import br.com.vaultfinance.api.web.dto.conta.ContaCreateRequest;
import br.com.vaultfinance.api.web.dto.conta.ContaResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contas")
public class ContaController {

  private final ContaService contaService;

  public ContaController(ContaService contaService) {
    this.contaService = contaService;
  }

  @PostMapping
  public ContaResponse criar(@RequestBody @Valid ContaCreateRequest req) {
    return contaService.criar(req);
  }

  @GetMapping("/usuario/{usuarioId}")
  public List<ContaResponse> listarPorUsuario(@PathVariable UUID usuarioId) {
    return contaService.listarPorUsuario(usuarioId);
  }
}