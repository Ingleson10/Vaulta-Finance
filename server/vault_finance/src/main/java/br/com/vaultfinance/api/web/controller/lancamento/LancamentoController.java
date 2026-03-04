package br.com.vaultfinance.api.web.controller.lancamento;

import br.com.vaultfinance.api.domain.lancamento.PeriodoPreset;
import br.com.vaultfinance.api.service.lancamento.LancamentoService;
import br.com.vaultfinance.api.web.dto.lancamento.LancamentoCreateRequest;
import br.com.vaultfinance.api.web.dto.lancamento.LancamentoResponse;
import br.com.vaultfinance.api.web.dto.lancamento.TransferenciaCreateRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {

  private final LancamentoService lancamentoService;

  public LancamentoController(LancamentoService lancamentoService) {
    this.lancamentoService = lancamentoService;
  }

  // ✅ Paginação + filtro por período
  // Ex 1: GET /api/lancamentos?page=0&size=10&inicio=2026-01-01&fim=2026-01-31
  // Ex 2: GET /api/lancamentos?page=0&size=10&periodo=MONTH
  // Ex 3: GET /api/lancamentos?page=0&size=10&periodo=LAST_30_DAYS
  @GetMapping
  public Page<LancamentoResponse> listar(
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
    @RequestParam(required = false) PeriodoPreset periodo,
    @PageableDefault(size = 10) Pageable pageable
  ) {
    return lancamentoService.listarPaginado(inicio, fim, periodo, pageable);
  }

  // ✅ Opcional: sem paginação
  @GetMapping("/all")
  public List<LancamentoResponse> listarTodos() {
    return lancamentoService.listarMeusLancamentos();
  }

  @PostMapping
  public LancamentoResponse criarReceitaOuDespesa(@RequestBody @Valid LancamentoCreateRequest req) {
    return lancamentoService.criarReceitaOuDespesa(req);
  }

  @PostMapping("/transferencias")
  public LancamentoResponse criarTransferencia(@RequestBody @Valid TransferenciaCreateRequest req) {
    return lancamentoService.criarTransferencia(req);
  }
}

