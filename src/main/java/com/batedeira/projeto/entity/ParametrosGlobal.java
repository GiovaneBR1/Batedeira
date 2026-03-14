package com.batedeira.projeto.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity //
public class ParametrosGlobal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Double toleranciaDef;
	
	@Column(name = "data_ultima_manutencao")
	private LocalDateTime dataUltimaManutencao;

	public LocalDateTime getDataUltimaManutencao() {
		return dataUltimaManutencao;
	}

	public void setDataUltimaManutencao(LocalDateTime dataUltimaManutencao) {
		this.dataUltimaManutencao = dataUltimaManutencao;
	}

	// Getters e Setters obrigatórios
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getToleranciaDef() {
		return toleranciaDef;
	}

	public void setToleranciaDef(Double toleranciaDef) {
		this.toleranciaDef = toleranciaDef;
	}
}