package br.com.vaultfinance.api.web.controller;

import br.com.vaultfinance.api.service.CategoriaService;
import br.com.vaultfinance.api.web.dto.categoria.CategoriaCreateRequest;
import br.com.vaultfinance.api.web.dto.categoria.CategoriaResponse;
import br.com.vaultfinance.api.web.dto.categoria.CategoriaUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

  private final CategoriaService categoriaService;

  public CategoriaController(CategoriaService categoriaService) {
    this.categoriaService = categoriaService;
  }

  @PostMapping
  public CategoriaResponse criar(@RequestBody @Valid CategoriaCreateRequest req) {
    return categoriaService.criar(req);
  }

  @GetMapping("/usuario/{usuarioId}")
  public List<CategoriaResponse> listarPorUsuario(@PathVariable UUID usuarioId) {
    return categoriaService.listarPorUsuario(usuarioId);
  }

  @GetMapping("/{id}")
  public CategoriaResponse buscarPorId(@PathVariable UUID id) {
    return categoriaService.buscarPorId(id);
  }

  @PutMapping("/{id}")
  public CategoriaResponse atualizar(@PathVariable UUID id, @RequestBody @Valid CategoriaUpdateRequest req) {
    return categoriaService.atualizar(id, req);
  }

  @DeleteMapping("/{id}")
  public void deletar(@PathVariable UUID id) {
    categoriaService.deletar(id);
  }
}