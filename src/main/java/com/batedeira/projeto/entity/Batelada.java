package com.batedeira.projeto.entity;

import com.batedeira.projeto.entity.enums.*;


//Importações do JPA (como nas outras entidades)
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.Enumerated; // (Para os Enums
import jakarta.persistence.EnumType; 
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;


/*
 -> Representa a tabela 'batelada' no banco de dados 
 ->Aqui contém os dados principais de uma batelada: hora de ínicio, hora de fim, 
   que receita usou, se foi no automático, se deu erro.
 */
@Entity
@Table(name = "batelada") 
public class Batelada {

	/**
	 -> Chave primária com auto-incremento
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	// colunas dos dados de hora de inicio e fim

	@Column(name = "data_inicio", nullable = false)
	private LocalDateTime dataInicio;

	@Column(name = "data_fim", nullable = false)
	private LocalDateTime dataFim;

	
	
	 /*
	 ->O "@Enumerated(EnumType.STRING)" garante que o banco salve 'MANUAL' e 'AUTOMATICO' 
	   ao invés de 0 e 1.
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "modo", nullable = false)
	private modoBatedeira modo; 

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private statusBatelada status; 

	@Column(name = "erro_mensagem", nullable = true)
	private String erroMensagem; // Para o status FORA_DE_TOLERANCIA ou ERRO
	
	@Column(name = "sobra_anterior")
    private Double sobraAnterior; //se havia resto no copo

    @Column(name = "minutos_ocioso_anterior")
    private Long minutosOciosoAnterior; // Quanto tempo a máquina ficou parada antes dessa batida?

    @Column(name = "iniciou_com_tanque_limpo")
    private Boolean iniciouComTanqueLimpo; // O Service achou que estava limpo?


	/*
	 * (6) @ManyToOne e @JoinColumn: 
	 * Dizemos ao JPA: "MUITAS (Many) Bateladas
	 * executam UMA (One) Receita."
	 */

	@ManyToOne(fetch = FetchType.LAZY)


	/*
	 * Usamos "receita_id" como a coluna da Chave Estrangeira (FK)
	 */
	@JoinColumn(name = "receita_id", nullable = true)
	private Receita receita; // (7) O objeto Java da Receita	
	
	
	@OneToMany(mappedBy = "batelada", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EtapaExecutada> etapasExecutadas = new ArrayList<>();

	public List<EtapaExecutada> getEtapasExecutadas() {
	    return etapasExecutadas;
	    
	    
	    
	}

	public void adicionarEtapaExecutada(EtapaExecutada etapa) {
	    this.etapasExecutadas.add(etapa);
	    etapa.setBatelada(this);
	}


	// --- Getters e Setters ---
	// (O JPA precisa deles)

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(LocalDateTime dataInicio) {
		this.dataInicio = dataInicio;
	}

	public LocalDateTime getDataFim() {
		return dataFim;
	}

	public void setDataFim(LocalDateTime dataFim) {
		this.dataFim = dataFim;
	}

	public modoBatedeira getModo() {
		return modo;
	}

	public void setModo(modoBatedeira modo) {
		this.modo = modo;
	}

	public statusBatelada getStatus() {
		return status;
	}

	public void setStatus(statusBatelada status) {
		this.status = status;
	}

	public String getErroMensagem() {
		return erroMensagem;
	}

	public void setErroMensagem(String erroMensagem) {
		this.erroMensagem = erroMensagem;
	}

	public Receita getReceita() {
		return receita;
	}

	public void setReceita(Receita receita) {
		this.receita = receita;
	}

	public Double getSobraAnterior() {
		return sobraAnterior;
	}

	public void setSobraAnterior(Double sobraAnterior) {
		this.sobraAnterior = sobraAnterior;
	}

	public Long getMinutosOciosoAnterior() {
		return minutosOciosoAnterior;
	}

	public void setMinutosOciosoAnterior(Long minutosOciosoAnterior) {
		this.minutosOciosoAnterior = minutosOciosoAnterior;
	}

	public Boolean getIniciouComTanqueLimpo() {
		return iniciouComTanqueLimpo;
	}

	public void setIniciouComTanqueLimpo(Boolean iniciouComTanqueLimpo) {
		this.iniciouComTanqueLimpo = iniciouComTanqueLimpo;
	}

	public void setEtapasExecutadas(List<EtapaExecutada> etapasExecutadas) {
		this.etapasExecutadas = etapasExecutadas;
	}
	
}
