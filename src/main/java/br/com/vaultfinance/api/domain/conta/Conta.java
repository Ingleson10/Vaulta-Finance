package br.com.vaultfinance.api.domain.conta;

import br.com.vaultfinance.api.domain.usuario.Usuario;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "conta", uniqueConstraints = @UniqueConstraint(name = "uq_conta_usuario_nome", columnNames = {
		"usuario_id", "nome" }))
public class Conta {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;

	@Column(name = "nome", nullable = false, length = 80)
	private String nome;

	@Column(name = "tipo", nullable = false)
	private String tipo; // depois evoluir para Enum

	@Column(name = "moeda", nullable = false, length = 3)
	private String moeda = "BRL";

	@Column(name = "saldo_inicial", nullable = false, precision = 14, scale = 2)
	private BigDecimal saldoInicial = BigDecimal.ZERO;

	@Column(name = "arquivada", nullable = false)
	private boolean arquivada = false;

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

	public UUID getId() {
		return id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getMoeda() {
		return moeda;
	}

	public void setMoeda(String moeda) {
		this.moeda = moeda;
	}

	public BigDecimal getSaldoInicial() {
		return saldoInicial;
	}

	public void setSaldoInicial(BigDecimal saldoInicial) {
		this.saldoInicial = saldoInicial;
	}

	public boolean isArquivada() {
		return arquivada;
	}

	public void setArquivada(boolean arquivada) {
		this.arquivada = arquivada;
	}
}
