package com.batedeira.projeto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List; // Precisamos importar a List


/*BateladaRequestDTO é o RELATÓRIO COMPLETO DA MISSÃO que o ESP32 envia para o backend.

Quando o BateladaService recebe esse Relatório Completo (BateladaRequestDTO), ele faz o seguinte:

Ele lê os "dados gerais" (dataInicio, etc.) e usa para criar a entidade Batelada.

Ele lê a lista de "Relatórios de Desempenho" (List<EtapaRequestDTO>).

Para cada item da lista (cada EtapaRequestDTO), separa:

O "Fato" (quantidadeReal) -> Salva na entidade EtapaExecutada.

O "Plano" (quantidadeEsperada, etc.) -> Usa para validar contra (ou criar) a entidade EtapaReceita.

O EtapaRequestDTO carrega o "Plano" (Receita) e o BateladaRequestDTO carrega os "Fatos" (Batelada).
 A única questão é que o BateladaRequestDTO carrega ambos, aninhados.

 **
 * DTO Principal: Mapeia o JSON enviado pelo ESP32 para /api/v1/bateladas
 * 
 */
@Schema(description = "DTO Principal para o JSON enviado pelo ESP32")
public class BateladaRequestDTO {

	/*
	 * O Service vai usar esses campos para preencher a entidade 'Batelada'
	 */

	@Schema(description = "ID da receita vindo do CLP (Chave de Negócio)", example = "22")
	private Long receitaId;

	@Schema(description = "Data/Hora de início no formato ISO 8601 UTC", example = "2025-11-01T15:30:00Z")
	private String dataInicio;

	@Schema(description = "Data/Hora de fim no formato ISO 8601 UTC", example = "2025-11-01T15:45:15Z")
	private String dataFim;

	@Schema(description = "Modo de operação da máquina", example = "AUTOMATICO")
	private String modo;
	
	@Schema(description = "Peso lido pelo sensor ANTES de iniciar", example = "20.5")
	private Double sobraAnterior;
	/*
	 * Esta é a lista de "Itens Individuais" (dados brutos) recebidos do ESP32.
	 * * O Service vai ler esta lista para fazer DUAS coisas:
	 * 1. Criar a entidade 'EtapaExecutada' (O FATO) -> usando o 'quantidadeReal'.
	 * 2. Criar/Validar a entidade 'EtapaReceita' (O PLANO) -> usando o 'quantidadeEsperada'.
	 */
	private List<EtapaRequestDTO> etapas;

	// --- Getters e Setters ---

	public Long getReceitaId() {
		return receitaId;
	}

	public void setReceitaId(Long receitaId) {
		this.receitaId = receitaId;
	}

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

