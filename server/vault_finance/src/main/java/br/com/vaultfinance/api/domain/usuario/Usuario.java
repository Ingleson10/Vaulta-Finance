package br.com.vaultfinance.api.domain.usuario;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "usuario")
public class Usuario {

  @Id
  @GeneratedValue
  private UUID id;

  @Column(name = "nome", nullable = false, length = 120)
  private String nome;

  @Column(name = "email", nullable = false, length = 180, unique = true)
  private String email;

  @Column(name = "senha_hash", nullable = false, length = 255)
  private String senhaHash;

  @Column(name = "ativo", nullable = false)
  private boolean ativo = true;

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
  public void setId(UUID id) { this.id = id; }
  public String getNome() { return nome; }
  public void setNome(String nome) { this.nome = nome; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getSenhaHash() { return senhaHash; }
  public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
  public boolean isAtivo() { return ativo; }
  public void setAtivo(boolean ativo) { this.ativo = ativo; }
  public OffsetDateTime getCriadoEm() { return criadoEm; }
  public OffsetDateTime getAtualizadoEm() { return atualizadoEm; }
}
