package com.batedeira.projeto.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import com.fasterxml.jackson.annotation.JsonIgnore;
/* Representa a tabela 'etapa_executada' no banco. 
 * Armazena individualmente os dados brutos medidos pelos sensores 
 * EX: "Etapa 1: 150.7kg medidos" (valor que o sensor na batedeira mediu)
 */

/**
 * (1) @Entity: Marca como "Documento Oficial" (tabela).
 * Esta entidade armazena o "Fato" (o que realmente foi medido).
 */
@Entity
@Table(name = "etapa_executada") // Mapeia para a tabela do MER
public class EtapaExecutada {

	/**
	 * (2) @Id e @GeneratedValue: Chave Primária (PK) com Auto-Incremento.
	 * o banco gera um novo id para cada novo registro salvo
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	// Colunas de dados 

	@Column(name = "ordem", nullable = false)
	private int ordem; //(etapa 1, etapa 2, etc...

	/**
	 * (3) A medição bruta do sensor.
	 * Esta é a informação que o Service vai comparar com o 'quantidadeEsperada' da EtapaReceita
	 */
	@Column(name = "quantidade_real", nullable = false)
	private double quantidadeReal; //EX: 152.2


	// --- O RELACIONAMENTO (A Conexão com a Batelada) ---

	/*
	 * (4) @ManyToOne e @JoinColumn: A Chave Estrangeira (FK).
	 * Dizemos ao JPA: "MUITAS (Many) EtapaExecutada
	  pertencem A UMA BATELADA Muitos registros de etapa pertencem a uma batelada" 
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	
	

	/* Diz ao JPA: A coluna no banco que faz a ligação se chama "batelada_id"
	 * "nullable = false" garante que um registro de etapa sempre esteja atrelado a uma batelada.
	 */
	@JoinColumn(name = "batelada_id", nullable = false)
	@JsonIgnore
	private Batelada batelada; // (5) O objeto Java da Batelada
	

	// --- Getters e Setters ---
	// (O JPA precisa deles)

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	public double getQuantidadeReal() {
		return quantidadeReal;
	}

	public void setQuantidadeReal(double quantidadeReal) {
		this.quantidadeReal = quantidadeReal;
	}

	public Batelada getBatelada() {
		return batelada;
	}

	public void setBatelada(Batelada batelada) {
		this.batelada = batelada;
	}
}
