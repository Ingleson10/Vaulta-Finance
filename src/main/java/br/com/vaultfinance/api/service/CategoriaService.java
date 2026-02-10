package br.com.vaultfinance.api.service;

import br.com.vaultfinance.api.domain.categoria.Categoria;
import br.com.vaultfinance.api.repository.CategoriaRepository;
import br.com.vaultfinance.api.repository.UsuarioRepository;
import br.com.vaultfinance.api.web.dto.categoria.CategoriaCreateRequest;
import br.com.vaultfinance.api.web.dto.categoria.CategoriaResponse;
import br.com.vaultfinance.api.web.dto.categoria.CategoriaUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    var usuario = usuarioRepository.findById(req.usuarioId())
      .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

    if (categoriaRepository.existsByUsuarioIdAndNomeIgnoreCase(req.usuarioId(), req.nome())) {
      throw new IllegalArgumentException("Já existe uma categoria com esse nome para este usuário");
    }

    var c = new Categoria();
    c.setUsuario(usuario);
    c.setNome(req.nome());
    c.setTipo(req.tipo());
    c.setCor(req.cor());
    c.setIcone(req.icone());

    var salva = categoriaRepository.save(c);
    return toResponse(salva);
  }

  @Transactional(readOnly = true)
  public List<CategoriaResponse> listarPorUsuario(UUID usuarioId) {
    return categoriaRepository.findByUsuarioId(usuarioId).stream()
      .map(this::toResponse)
      .toList();
  }

  @Transactional(readOnly = true)
  public CategoriaResponse buscarPorId(UUID id) {
    return categoriaRepository.findById(id)
      .map(this::toResponse)
      .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
  }

  @Transactional
  public CategoriaResponse atualizar(UUID id, CategoriaUpdateRequest req) {
    var c = categoriaRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

    // Se mudou o nome, valida duplicidade
    if (!c.getNome().equalsIgnoreCase(req.nome())
        && categoriaRepository.existsByUsuarioIdAndNomeIgnoreCase(c.getUsuario().getId(), req.nome())) {
      throw new IllegalArgumentException("Já existe uma categoria com esse nome para este usuário");
    }

    c.setNome(req.nome());
    c.setTipo(req.tipo());
    c.setCor(req.cor());
    c.setIcone(req.icone());

    return toResponse(categoriaRepository.save(c));
  }

  @Transactional
  public void deletar(UUID id) {
    if (!categoriaRepository.existsById(id)) {
      throw new IllegalArgumentException("Categoria não encontrada");
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
