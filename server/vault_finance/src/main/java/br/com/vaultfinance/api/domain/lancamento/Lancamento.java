package br.com.vaultfinance.api.domain.lancamento;

import br.com.vaultfinance.api.domain.usuario.Usuario;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "lancamento")
public class Lancamento {

  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  @Column(name = "descricao", length = 200)
  private String descricao;

  // Postgres enum: tipo_categoria (RECEITA | DESPESA)
  @Enumerated(EnumType.STRING)
  @Column(name = "tipo", nullable = false, columnDefinition = "tipo_categoria")
  private TipoCategoria tipo;

  // Postgres enum: status_lancamento (PENDENTE | CONFIRMADO | CANCELADO)
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, columnDefinition = "status_lancamento")
  private StatusLancamento status = StatusLancamento.CONFIRMADO;

  @Column(name = "data_ocorrencia", nullable = false)
  private LocalDate dataOcorrencia;

  // DDL: observacoes text (plural)
  @Column(name = "observacoes", columnDefinition = "text")
  private String observacoes;

  @Column(name = "criado_em", nullable = false)
  private OffsetDateTime criadoEm;

  @Column(name = "atualizado_em", nullable = false)
  private OffsetDateTime atualizadoEm;

  @PrePersist
  void prePersist() {
    var now = OffsetDateTime.now();
    criadoEm = now;
    atualizadoEm = now;
  }

  @PreUpdate
  void preUpdate() {
    atualizadoEm = OffsetDateTime.now();
  }

  // getters/setters
  public UUID getId() { return id; }

  public Usuario getUsuario() { return usuario; }
  public void setUsuario(Usuario usuario) { this.usuario = usuario; }

  public String getDescricao() { return descricao; }
  public void setDescricao(String descricao) { this.descricao = descricao; }

  public TipoCategoria getTipo() { return tipo; }
  public void setTipo(TipoCategoria tipo) { this.tipo = tipo; }

  public StatusLancamento getStatus() { return status; }
  public void setStatus(StatusLancamento status) { this.status = status; }

  public LocalDate getDataOcorrencia() { return dataOcorrencia; }
  public void setDataOcorrencia(LocalDate dataOcorrencia) { this.dataOcorrencia = dataOcorrencia; }

  public String getObservacoes() { return observacoes; }
  public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

  public OffsetDateTime getCriadoEm() { return criadoEm; }
  public OffsetDateTime getAtualizadoEm() { return atualizadoEm; }
}
