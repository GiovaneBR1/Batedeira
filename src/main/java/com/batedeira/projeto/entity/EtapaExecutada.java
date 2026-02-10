package com.batedeira.projeto.entity;

import com.batedeira.projeto.entity.enums.StatusEtapa;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
 
@Entity
public class EtapaExecutada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //deixa a cargo do banco a criação de ID's
    @JsonIgnore
    private Long id;

    private Integer ordem; 

    private String ingredienteNome; // de acordo com o motor que ligou
    
    private Double quantidadeEsperada; // O Setpoint do CLP
    
    private Double quantidadeReal;     // O que pesou de fato
    
    private String unidade;            // "KG", "L", etc.

    @Enumerated(EnumType.STRING)
    private StatusEtapa status;        // OK ou ERRO 

    // --- RELACIONAMENTO ---
    @ManyToOne
    @JoinColumn(name = "batelada_id")
    @JsonIgnore //para não criar um relatorio em loop
    private Batelada batelada;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public String getIngredienteNome() {
		return ingredienteNome;
	}

	public void setIngredienteNome(String ingredienteNome) {
		this.ingredienteNome = ingredienteNome;
	}

	public Double getQuantidadeEsperada() {
		return quantidadeEsperada;
	}

	public void setQuantidadeEsperada(Double quantidadeEsperada) {
		this.quantidadeEsperada = quantidadeEsperada;
	}

	public Double getQuantidadeReal() {
		return quantidadeReal;
	}

	public void setQuantidadeReal(Double quantidadeReal) {
		this.quantidadeReal = quantidadeReal;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public StatusEtapa getStatus() {
		return status;
	}

	public void setStatus(StatusEtapa status) {
		this.status = status;
	}

	public Batelada getBatelada() {
		return batelada;
	}

	public void setBatelada(Batelada batelada) {
		this.batelada = batelada;
	}

	public EtapaExecutada(Long id, Integer ordem, String ingredienteNome, Double quantidadeEsperada,
			Double quantidadeReal, String unidade, StatusEtapa status, Batelada batelada) {
		super();
		this.id = id;
		this.ordem = ordem;
		this.ingredienteNome = ingredienteNome;
		this.quantidadeEsperada = quantidadeEsperada;
		this.quantidadeReal = quantidadeReal;
		this.unidade = unidade;
		this.status = status;
		this.batelada = batelada;
	}

	public EtapaExecutada() {
		
	}
}