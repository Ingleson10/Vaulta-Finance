package br.com.vaultfinance.api.service;

import br.com.vaultfinance.api.domain.categoria.Categoria;
import br.com.vaultfinance.api.domain.exception.BusinessException;
import br.com.vaultfinance.api.domain.exception.NotFoundException;
import br.com.vaultfinance.api.repository.CategoriaRepository;
import br.com.vaultfinance.api.repository.UsuarioRepository;
import br.com.vaultfinance.api.security.SecurityUtils;
import br.com.vaultfinance.api.web.dto.categoria.CategoriaCreateRequest;
import br.com.vaultfinance.api.web.dto.categoria.CategoriaResponse;
import br.com.vaultfinance.api.web.dto.categoria.CategoriaUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CategoriaService {

  private final CategoriaRepository categoriaRepository;
  private final UsuarioRepository usuarioRepository;

  public CategoriaService(CategoriaRepository categoriaRepository, UsuarioRepository usuarioRepository) {
    this.categoriaRepository = categoriaRepository;
    this.usuarioRepository = usuarioRepository;
  }

  @Transactional
  public CategoriaResponse criar(CategoriaCreateRequest req) {
    UUID usuarioId = SecurityUtils.getUsuarioId();

    var usuario = usuarioRepository.findById(usuarioId)
      .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

    if (categoriaRepository.existsByUsuarioIdAndNomeIgnoreCase(usuarioId, req.nome())) {
      throw new BusinessException("Já existe uma categoria com esse nome para este usuário");
    }

    var c = new Categoria();
    c.setUsuario(usuario);
    c.setNome(req.nome());
    c.setTipo(req.tipo());
    c.setCor(req.cor());
    c.setIcone(req.icone());

    return toResponse(categoriaRepository.save(c));
  }

  @Transactional(readOnly = true)
  public Page<CategoriaResponse> listarMinhasPaginado(Pageable pageable) {
    UUID usuarioId = SecurityUtils.getUsuarioId();
    return categoriaRepository.findAllByUsuarioId(usuarioId, pageable).map(this::toResponse);
  }

  @Transactional(readOnly = true)
  public CategoriaResponse buscarPorId(UUID id) {
    UUID usuarioId = SecurityUtils.getUsuarioId();

    return categoriaRepository.findByIdAndUsuarioId(id, usuarioId)
      .map(this::toResponse)
      .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));
  }

  @Transactional
  public CategoriaResponse atualizar(UUID id, CategoriaUpdateRequest req) {
    UUID usuarioId = SecurityUtils.getUsuarioId();

    var c = categoriaRepository.findByIdAndUsuarioId(id, usuarioId)
      .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));

    if (!c.getNome().equalsIgnoreCase(req.nome())
      && categoriaRepository.existsByUsuarioIdAndNomeIgnoreCase(usuarioId, req.nome())) {
      throw new BusinessException("Já existe uma categoria com esse nome para este usuário");
    }

    c.setNome(req.nome());
    c.setTipo(req.tipo());
    c.setCor(req.cor());
    c.setIcone(req.icone());

    return toResponse(categoriaRepository.save(c));
  }

  @Transactional
  public void deletar(UUID id) {
    UUID usuarioId = SecurityUtils.getUsuarioId();

    if (!categoriaRepository.existsByIdAndUsuarioId(id, usuarioId)) {
      throw new NotFoundException("Categoria não encontrada");
    }

    categoriaRepository.deleteById(id);
  }

  private CategoriaResponse toResponse(Categoria c) {
    return new CategoriaResponse(
      c.getId(),
      c.getUsuario().getId(),
      c.getNome(),
      c.getTipo(),
      c.getCor(),
      c.getIcone()
    );
  }
}
