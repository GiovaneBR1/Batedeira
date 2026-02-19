package com.batedeira.projeto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List; // Precisamos importar a List

@Schema(description = "DTO Principal para o JSON enviado pelo ESP32")
public class BateladaRequestDTO {

	// O Service vai usar esses campos para preencher a entidade 'Batelada'



	@Schema(description = "Data/Hora de início no formato ISO 8601 UTC", example = "2025-11-01T15:30:00Z")
	private String dataInicio;

	@Schema(description = "Data/Hora de fim no formato ISO 8601 UTC", example = "2025-11-01T15:45:15Z")
	private String dataFim;

	@Schema(description = "Modo de operação da máquina", example = "AUTOMATICO")
	private String modo;

	@Schema(description = "Peso lido pelo sensor ANTES de iniciar", example = "20.5")
	private Double sobraAnterior;

	
	private List<EtapaRequestDTO> etapas;


	public String getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(String dataInicio) {
		this.dataInicio = dataInicio;
	}

	public String getDataFim() {
		return dataFim;
	}

	public void setDataFim(String dataFim) {
		this.dataFim = dataFim;
	}

	public String getModo() {
		return modo;
	}

	public void setModo(String modo) {
		this.modo = modo;
	}

	public List<EtapaRequestDTO> getEtapas() {
		return etapas;
	}

	public void setEtapas(List<EtapaRequestDTO> etapas) {
		this.etapas = etapas;
	}

	public Double getSobraAnterior() {
		return sobraAnterior;
	}

	public void setSobraAnterior(Double sobraAnterior) {
		this.sobraAnterior = sobraAnterior;
	}

}
