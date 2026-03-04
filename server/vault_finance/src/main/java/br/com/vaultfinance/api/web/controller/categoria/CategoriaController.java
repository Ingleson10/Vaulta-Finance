package br.com.vaultfinance.api.web.controller.categoria;

import br.com.vaultfinance.api.service.categoria.CategoriaService;
import br.com.vaultfinance.api.web.dto.categoria.CategoriaCreateRequest;
import br.com.vaultfinance.api.web.dto.categoria.CategoriaResponse;
import br.com.vaultfinance.api.web.dto.categoria.CategoriaUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

  private final CategoriaService categoriaService;

  public CategoriaController(CategoriaService categoriaService) {
    this.categoriaService = categoriaService;
  }

  @GetMapping
  public Page<CategoriaResponse> listar(@PageableDefault(size = 10) Pageable pageable) {
    return categoriaService.listarMinhasPaginado(pageable);
  }

  @GetMapping("/{id}")
  public CategoriaResponse buscar(@PathVariable UUID id) {
    return categoriaService.buscarPorId(id);
  }

  @PostMapping
  public CategoriaResponse criar(@RequestBody @Valid CategoriaCreateRequest req) {
    return categoriaService.criar(req);
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
