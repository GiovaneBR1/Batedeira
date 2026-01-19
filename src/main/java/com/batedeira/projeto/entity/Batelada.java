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


/* Objetivo: Ele representa (mapeia) a tabela 'batelada' do
	  nosso banco (o MER). É o "Relatório de Execução"
	  (ex: "A execução da máquina das 14:30 às 14:45").

  	(1) @Entity: Marca como (tabela).
 */
@Entity
@Table(name = "batelada") // Mapeia para a tabela do MER
public class Batelada {

	/**
	 * (2) @Id e @GeneratedValue: Chave Primária (PK) com Auto-Incremento.
	 * O sistema gera o ID para cada nova batelada.
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

	
	
	/**
	 * (3) @Enumerated: Como o Java deve salvar o Enum no banco.
	 * (EnumType.STRING) diz: "Salve o TEXTO ('MANUAL', 'AUTOMATICO')".
	 * Isso é muito melhor do que salvar o número (0 ou 1),
	 * pois o banco fica mais fácil de ler.
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "modo", nullable = false)
	private modoBatedeira modo; // (4) Usa o modoBatedeira

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private statusBatelada status; // (5) Usa o statusBatelada

	@Column(name = "erro_mensagem", nullable = true)
	private String erroMensagem; // (Para o status FORA_DE_TOLERANCIA ou ERRO)
	
	@Column(name = "sobra_anterior")
    private Double sobraAnterior; // Quanto tinha de peso no inicio?

    @Column(name = "minutos_ocioso_anterior")
    private Long minutosOciosoAnterior; // Quanto tempo a máquina ficou parada antes dessa batida?

    @Column(name = "iniciou_com_tanque_limpo")
    private Boolean iniciouComTanqueLimpo; // O Service achou que estava limpo?


	// --- O RELACIONAMENTO (A Conexão com a Receita) ---

	/**
	 * (6) @ManyToOne e @JoinColumn: Exatamente como na EtapaReceita.
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
