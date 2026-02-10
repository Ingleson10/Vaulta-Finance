package br.com.vaultfinance.api.service;

import br.com.vaultfinance.api.domain.conta.Conta;
import br.com.vaultfinance.api.repository.ContaRepository;
import br.com.vaultfinance.api.repository.UsuarioRepository;
import br.com.vaultfinance.api.web.dto.conta.ContaCreateRequest;
import br.com.vaultfinance.api.web.dto.conta.ContaResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
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
    var usuario = usuarioRepository.findById(req.usuarioId())
      .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

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
  public List<ContaResponse> listarPorUsuario(UUID usuarioId) {
    return contaRepository.findByUsuarioId(usuarioId).stream()
      .map(this::toResponse)
      .toList();
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