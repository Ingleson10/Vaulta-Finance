package br.com.vaultfinance.api.domain.categoria;

import br.com.vaultfinance.api.domain.usuario.Usuario;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "categoria",
  uniqueConstraints = @UniqueConstraint(name = "uq_categoria_usuario_nome", columnNames = {"usuario_id", "nome"})
)
public class Categoria {

  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  @Column(name = "nome", nullable = false, length = 60)
  private String nome;

  @Column(name = "tipo", nullable = false)
  private String tipo; // RECEITA / DESPESA (depois Enum)

  @Column(name = "cor", length = 12)
  private String cor;

  @Column(name = "icone", length = 40)
  private String icone;

  @Column(name = "criada_em", nullable = false)
  private OffsetDateTime criadaEm;

  @PrePersist
  void prePersist() {
    criadaEm = OffsetDateTime.now();
  }

  public UUID getId() { return id; }
  public Usuario getUsuario() { return usuario; }
  public void setUsuario(Usuario usuario) { this.usuario = usuario; }
  public String getNome() { return nome; }
  public void setNome(String nome) { this.nome = nome; }
  public String getTipo() { return tipo; }
  public void setTipo(String tipo) { this.tipo = tipo; }
  public String getCor() { return cor; }
  public void setCor(String cor) { this.cor = cor; }
  public String getIcone() { return icone; }
  public void setIcone(String icone) { this.icone = icone; }
}
