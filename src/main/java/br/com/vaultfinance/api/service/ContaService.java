package br.com.vaultfinance.api.service;

import br.com.vaultfinance.api.domain.conta.Conta;
import br.com.vaultfinance.api.domain.exception.NotFoundException;
import br.com.vaultfinance.api.repository.ContaRepository;
import br.com.vaultfinance.api.repository.UsuarioRepository;
import br.com.vaultfinance.api.security.SecurityUtils;
import br.com.vaultfinance.api.web.dto.conta.ContaCreateRequest;
import br.com.vaultfinance.api.web.dto.conta.ContaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ContaService {

  private final ContaRepository contaRepository;
  private final UsuarioRepository usuarioRepository;

  public ContaService(ContaRepository contaRepository, UsuarioRepository usuarioRepository) {
    this.contaRepository = contaRepository;
    this.usuarioRepository = usuarioRepository;
  }

  @Transactional
  public ContaResponse criar(ContaCreateRequest req) {
    UUID usuarioId = SecurityUtils.getUsuarioId();

    var usuario = usuarioRepository.findById(usuarioId)
      .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

    var conta = new Conta();
    conta.setUsuario(usuario);
    conta.setNome(req.nome());
    conta.setTipo(req.tipo());
    conta.setMoeda(req.moeda() != null ? req.moeda() : "BRL");
    conta.setSaldoInicial(req.saldoInicial() != null ? req.saldoInicial() : BigDecimal.ZERO);
    conta.setArquivada(false);

    var salva = contaRepository.save(conta);
    return toResponse(salva);
  }

  @Transactional(readOnly = true)
  public Page<ContaResponse> listarMinhasPaginado(Pageable pageable) {
    UUID usuarioId = SecurityUtils.getUsuarioId();
    return contaRepository.findAllByUsuarioId(usuarioId, pageable).map(this::toResponse);
  }

  @Transactional(readOnly = true)
  public ContaResponse buscarPorId(UUID id) {
    UUID usuarioId = SecurityUtils.getUsuarioId();

    return contaRepository.findByIdAndUsuarioId(id, usuarioId)
      .map(this::toResponse)
      .orElseThrow(() -> new NotFoundException("Conta não encontrada"));
  }

  @Transactional
  public void deletar(UUID id) {
    UUID usuarioId = SecurityUtils.getUsuarioId();

    if (!contaRepository.existsByIdAndUsuarioId(id, usuarioId)) {
      throw new NotFoundException("Conta não encontrada");
    }

    contaRepository.deleteById(id);
  }

  private ContaResponse toResponse(Conta c) {
    return new ContaResponse(
      c.getId(),
      c.getUsuario().getId(),
      c.getNome(),
      c.getTipo(),
      c.getMoeda(),
      c.getSaldoInicial(),
      c.isArquivada()
    );
  }
}
