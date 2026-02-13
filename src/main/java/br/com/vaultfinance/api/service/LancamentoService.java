package br.com.vaultfinance.api.service;

import br.com.vaultfinance.api.domain.exception.BusinessException;
import br.com.vaultfinance.api.domain.exception.NotFoundException;
import br.com.vaultfinance.api.domain.lancamento.Lancamento;
import br.com.vaultfinance.api.domain.lancamento.PeriodoPreset;
import br.com.vaultfinance.api.domain.lancamento.PeriodoUtils;
import br.com.vaultfinance.api.domain.lancamento.StatusLancamento;
import br.com.vaultfinance.api.domain.lancamento.TipoCategoria;
import br.com.vaultfinance.api.domain.movimentacao.Movimentacao;
import br.com.vaultfinance.api.repository.*;
import br.com.vaultfinance.api.security.SecurityUtils;
import br.com.vaultfinance.api.web.dto.lancamento.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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

  // ✅ Paginação + filtro por período
  // Regras:
  // - Se inicio/fim vierem, tem prioridade
  // - Senão usa "periodo" (MONTH, LAST_30_DAYS, YEAR)
  // - Se nada vier, lista tudo paginado
  @Transactional(readOnly = true)
  public Page<LancamentoResponse> listarPaginado(LocalDate inicio, LocalDate fim, PeriodoPreset periodo, Pageable pageable) {
    UUID usuarioId = SecurityUtils.getUsuarioId();

    // se não veio inicio/fim, tenta resolver pelo preset
    if (inicio == null && fim == null && periodo != null && periodo != PeriodoPreset.ALL) {
      LocalDate[] intervalo = PeriodoUtils.resolver(periodo);
      inicio = intervalo[0];
      fim = intervalo[1];
    }

    // se veio só um dos dois, aplica regra de negócio
    if ((inicio == null) != (fim == null)) {
      throw new BusinessException("Informe inicio e fim juntos, ou use o parâmetro periodo");
    }

    Page<Lancamento> page;

    if (inicio != null && fim != null) {
      page = lancamentoRepository.findAllByUsuarioIdAndDataOcorrenciaBetween(usuarioId, inicio, fim, pageable);
    } else {
      page = lancamentoRepository.findAllByUsuarioId(usuarioId, pageable);
    }

    return page.map(l -> {
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
    });
  }

  @Transactional
  public LancamentoResponse criarReceitaOuDespesa(LancamentoCreateRequest req) {

    UUID usuarioId = SecurityUtils.getUsuarioId();

    var usuario = usuarioRepository.findById(usuarioId)
      .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

    var conta = contaRepository.findByIdAndUsuarioId(req.contaId(), usuarioId)
      .orElseThrow(() -> new NotFoundException("Conta não encontrada"));

    var lanc = new Lancamento();
    lanc.setUsuario(usuario);
    lanc.setDescricao(req.descricao());
    lanc.setTipo(req.tipo());
    lanc.setStatus(StatusLancamento.CONFIRMADO);
    lanc.setDataOcorrencia(req.dataOcorrencia());
    lanc.setObservacoes(req.observacoes());

    var lancSalvo = lancamentoRepository.save(lanc);

    var mov = new Movimentacao();
    mov.setLancamento(lancSalvo);
    mov.setConta(conta);

    if (req.categoriaId() != null) {
      var cat = categoriaRepository.findByIdAndUsuarioId(req.categoriaId(), usuarioId)
        .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));
      mov.setCategoria(cat);
    }

    mov.setNatureza(req.tipo() == TipoCategoria.RECEITA ? "CREDITO" : "DEBITO");
    mov.setValor(req.valor());

    var movSalva = movimentacaoRepository.save(mov);

    BigDecimal saldo = movimentacaoRepository.calcularSaldoAtual(conta.getId());

    return toResponse(lancSalvo, List.of(movSalva), saldo);
  }

  @Transactional
  public LancamentoResponse criarTransferencia(TransferenciaCreateRequest req) {

    UUID usuarioId = SecurityUtils.getUsuarioId();

    var usuario = usuarioRepository.findById(usuarioId)
      .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

    if (req.contaOrigemId().equals(req.contaDestinoId())) {
      throw new BusinessException("Conta origem e destino não podem ser iguais");
    }

    var origem = contaRepository.findByIdAndUsuarioId(req.contaOrigemId(), usuarioId)
      .orElseThrow(() -> new NotFoundException("Conta origem não encontrada"));

    var destino = contaRepository.findByIdAndUsuarioId(req.contaDestinoId(), usuarioId)
      .orElseThrow(() -> new NotFoundException("Conta destino não encontrada"));

    var lanc = new Lancamento();
    lanc.setUsuario(usuario);
    lanc.setDescricao(req.descricao());
    lanc.setStatus(StatusLancamento.CONFIRMADO);
    lanc.setDataOcorrencia(req.dataOcorrencia());
    lanc.setObservacoes(req.observacoes());

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
