package br.com.vaultfinance.api.service;
import br.com.vaultfinance.api.security.SecurityUtils;

import br.com.vaultfinance.api.domain.lancamento.Lancamento;
import br.com.vaultfinance.api.domain.lancamento.StatusLancamento;
import br.com.vaultfinance.api.domain.lancamento.TipoCategoria;
import br.com.vaultfinance.api.domain.movimentacao.Movimentacao;
import br.com.vaultfinance.api.repository.*;
import br.com.vaultfinance.api.web.dto.lancamento.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class LancamentoService {

  private final LancamentoRepository lancamentoRepository;
  private final MovimentacaoRepository movimentacaoRepository;
  private final UsuarioRepository usuarioRepository;
  private final ContaRepository contaRepository;
  private final CategoriaRepository categoriaRepository;

  public LancamentoService(
    LancamentoRepository lancamentoRepository,
    MovimentacaoRepository movimentacaoRepository,
    UsuarioRepository usuarioRepository,
    ContaRepository contaRepository,
    CategoriaRepository categoriaRepository
  ) {
    this.lancamentoRepository = lancamentoRepository;
    this.movimentacaoRepository = movimentacaoRepository;
    this.usuarioRepository = usuarioRepository;
    this.contaRepository = contaRepository;
    this.categoriaRepository = categoriaRepository;
  }

  @Transactional
  public LancamentoResponse criarReceitaOuDespesa(LancamentoCreateRequest req) {
	 var usuarioId = SecurityUtils.getUsuarioId();
	 var usuario = usuarioRepository.findById(usuarioId)
			    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

    var conta = contaRepository.findById(req.contaId())
      .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));

    if (!conta.getUsuario().getId().equals(usuario.getId())) {
      throw new IllegalArgumentException("Conta não pertence ao usuário");
    }

    var lanc = new Lancamento();
    lanc.setUsuario(usuario);
    lanc.setDescricao(req.descricao());
    lanc.setTipo(req.tipo()); // TipoCategoria (RECEITA/DESPESA)
    lanc.setStatus(StatusLancamento.CONFIRMADO);
    lanc.setDataOcorrencia(req.dataOcorrencia());
    lanc.setObservacoes(req.observacoes());

    var lancSalvo = lancamentoRepository.save(lanc);

    var mov = new Movimentacao();
    mov.setLancamento(lancSalvo);
    mov.setConta(conta);

    if (req.categoriaId() != null) {
      var cat = categoriaRepository.findById(req.categoriaId())
        .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
      if (!cat.getUsuario().getId().equals(usuario.getId())) {
        throw new IllegalArgumentException("Categoria não pertence ao usuário");
      }
      mov.setCategoria(cat);
    }

    // Receita = crédito / Despesa = débito
    if (req.tipo() == TipoCategoria.RECEITA) {
      mov.setNatureza("CREDITO");
    } else if (req.tipo() == TipoCategoria.DESPESA) {
      mov.setNatureza("DEBITO");
    } else {
      // deve ser impossível com enum, mas mantive por segurança
      throw new IllegalArgumentException("Tipo inválido. Use RECEITA ou DESPESA");
    }

    mov.setValor(req.valor());
    var movSalva = movimentacaoRepository.save(mov);

    BigDecimal saldo = movimentacaoRepository.calcularSaldoAtual(conta.getId());

    return toResponse(lancSalvo, List.of(movSalva), saldo);
  }

  @Transactional
  public LancamentoResponse criarTransferencia(TransferenciaCreateRequest req) {
	  var usuarioId = SecurityUtils.getUsuarioId();

	  var usuario = usuarioRepository.findById(usuarioId)
	    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

    if (req.contaOrigemId().equals(req.contaDestinoId())) {
      throw new IllegalArgumentException("Conta origem e destino não podem ser iguais");
    }

    var origem = contaRepository.findById(req.contaOrigemId())
      .orElseThrow(() -> new IllegalArgumentException("Conta origem não encontrada"));

    var destino = contaRepository.findById(req.contaDestinoId())
      .orElseThrow(() -> new IllegalArgumentException("Conta destino não encontrada"));

    if (!origem.getUsuario().getId().equals(usuario.getId())
      || !destino.getUsuario().getId().equals(usuario.getId())) {
      throw new IllegalArgumentException("As contas devem pertencer ao usuário");
    }

    var lanc = new Lancamento();
    lanc.setUsuario(usuario);
    lanc.setDescricao(req.descricao());

    // IMPORTANTE:
    // Seu enum TipoCategoria (e o tipo Postgres tipo_categoria) tem RECEITA/DESPESA.
    // Transferência não encaixa aqui sem mudar o enum no banco.
    // Então não setamos "tipo" para transferência.
    // Se você quiser MUITO registrar isso, crie um novo enum/coluna tipo_lancamento.
    // lanc.setTipo(...);

    lanc.setStatus(StatusLancamento.CONFIRMADO);
    lanc.setDataOcorrencia(req.dataOcorrencia());
    lanc.setObservacoes(req.observacoes()); // ajuste conforme seu TransferenciaCreateRequest

    var lancSalvo = lancamentoRepository.save(lanc);

    var debito = new Movimentacao();
    debito.setLancamento(lancSalvo);
    debito.setConta(origem);
    debito.setNatureza("DEBITO");
    debito.setValor(req.valor());

    var credito = new Movimentacao();
    credito.setLancamento(lancSalvo);
    credito.setConta(destino);
    credito.setNatureza("CREDITO");
    credito.setValor(req.valor());

    var m1 = movimentacaoRepository.save(debito);
    var m2 = movimentacaoRepository.save(credito);

    BigDecimal saldoOrigem = movimentacaoRepository.calcularSaldoAtual(origem.getId());

    return toResponse(lancSalvo, List.of(m1, m2), saldoOrigem);
  }
  
  @Transactional(readOnly = true)
  public List<LancamentoResponse> listarMeusLancamentos() {
    UUID usuarioId = SecurityUtils.getUsuarioId();
    return listarPorUsuario(usuarioId);
  }

  @Transactional(readOnly = true)
  public List<LancamentoResponse> listarPorUsuario(UUID usuarioId) {
    return lancamentoRepository.findByUsuarioId(usuarioId).stream()
      .map(l -> {
        var movs = movimentacaoRepository.findByLancamentoId(l.getId());
        var movResponses = movs.stream().map(m -> new MovimentacaoResponse(
          m.getId(),
          m.getConta().getId(),
          m.getCategoria() != null ? m.getCategoria().getId() : null,
          m.getNatureza(),
          m.getValor()
        )).toList();

        return new LancamentoResponse(
          l.getId(),
          l.getUsuario().getId(),
          l.getDescricao(),
          l.getTipo(),
          l.getStatus(),
          l.getDataOcorrencia(),
          l.getObservacoes(),
          movResponses,
          null
        );
      })
      .toList();
  }

  private LancamentoResponse toResponse(Lancamento l, List<Movimentacao> movs, BigDecimal saldoConta) {
    var movResponses = movs.stream().map(m -> new MovimentacaoResponse(
      m.getId(),
      m.getConta().getId(),
      m.getCategoria() != null ? m.getCategoria().getId() : null,
      m.getNatureza(),
      m.getValor()
    )).toList();

    return new LancamentoResponse(
      l.getId(),
      l.getUsuario().getId(),
      l.getDescricao(),
      l.getTipo(),
      l.getStatus(),
      l.getDataOcorrencia(),
      l.getObservacoes(),
      movResponses,
      saldoConta
    );
  }
}