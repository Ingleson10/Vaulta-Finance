package br.com.vaultfinance.api.web.controller;

import br.com.vaultfinance.api.service.ContaService;
import br.com.vaultfinance.api.web.dto.conta.ContaCreateRequest;
import br.com.vaultfinance.api.web.dto.conta.ContaResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/contas")
public class ContaController {

  private final ContaService contaService;

  public ContaController(ContaService contaService) {
    this.contaService = contaService;
  }

  @GetMapping
  public Page<ContaResponse> listar(@PageableDefault(size = 10) Pageable pageable) {
    return contaService.listarMinhasPaginado(pageable);
  }

  @GetMapping("/{id}")
  public ContaResponse buscar(@PathVariable UUID id) {
    return contaService.buscarPorId(id);
  }

  @PostMapping
  public ContaResponse criar(@RequestBody @Valid ContaCreateRequest req) {
    return contaService.criar(req);
  }

  @DeleteMapping("/{id}")
  public void deletar(@PathVariable UUID id) {
    contaService.deletar(id);
  }
}
