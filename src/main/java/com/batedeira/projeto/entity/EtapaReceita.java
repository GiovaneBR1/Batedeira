package com.batedeira.projeto.entity;

//Importações do JPA
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue; // Para auto-incremento
import jakarta.persistence.GenerationType; 

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne; //  Para o relacionamento
import jakarta.persistence.JoinColumn; //  Para a Chave Estrangeira
import jakarta.persistence.FetchType; //  Otimização de Busca (evita carregamento desnecessário de objetos)

/* Representa a tabela "etapa_receita" do banco 
 * Os EtapaReceita são os registros no banco que dizem ao Service exatamente o que deveria acontecer, 
 * qual a ordem, qual a quantidade e qual a tolerância permitida.
*/
@Entity
@Table(name = "etapa_receita")
public class EtapaReceita {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)  //deixa a cargo do banco a criação de ID's
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "ordem", nullable = false)
	private int ordem;

	@Column(name = "quantidade_esperada", nullable = false)
	private double quantidadeEsperada; // definida no clp

	@Column(name = "unidade", nullable = false)
	private String unidade; // kg

	@Column(name = "tolerancia_percentual", nullable = true)
	private Double toleranciaPercentual; // admin vai definir

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receita_id", nullable = false) //nullable = false garante que nenhuma etapa exista sem uma receita-mãe
	@JsonIgnore
	private Receita receita; // (7) O objeto Java da "mãe"



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

	public double getQuantidadeEsperada() {
		return quantidadeEsperada;
	}

	public void setQuantidadeEsperada(double quantidadeEsperada) {
		this.quantidadeEsperada = quantidadeEsperada;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public Double getToleranciaPercentual() {
		return toleranciaPercentual;
	}

	public void setToleranciaPercentual(Double toleranciaPercentual) {
		this.toleranciaPercentual = toleranciaPercentual;
	}

	public Receita getReceita() {
		return receita;
	}

	public void setReceita(Receita receita) {
		this.receita = receita;
	}
}