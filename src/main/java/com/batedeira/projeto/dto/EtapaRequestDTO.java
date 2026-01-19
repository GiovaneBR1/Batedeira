package com.batedeira.projeto.dto;
import io.swagger.v3.oas.annotations.media.Schema;

//o @Schema serve somente para documentação
/*
 * Aqui cada etapa do processo de produção de cola é armazenada
 * EX:  Etapa 1: colocar 150kg de agua

		Etapa 2: colocar 80kg de cola

		Etapa 3: colocar 80kg de trigo
		
		A classe EtapaRequestDTO armazena uma por uma.
		
		EX: Sequencia do passo: 1
			Valor real medido pelo sensor: 150.0
			Descrição da etapa: inserir água
			Valor esperado: 150.0
			unidade de medida: kg
			
		OU SEJA: A etapa(sequencia do passo) 1 faz o seguinte: Colocou 150kg de água
		O valor esperado é para controle de tolerância.
 */


@Schema(description = "DTO Aninhado para os dados de cada etapa")
public class EtapaRequestDTO {
		/**
	 * DTO Aninhado: Representa cada etapa individual dentro da lista "etapas"
	 * (Esse é o formulário que o ESP32 preenche e envia para o Controller)
	 */

	    @Schema(description = "Sequência do passo", example = "1")
	    private Integer ordem;

	    @Schema(description = "Valor real medido pelo sensor", example = "150.5")
	    private Double quantidadeReal;

	    // --- Dados para auto-criação da EtapaReceita (O Plano) ---
	    @Schema(description = "Descrição da etapa", example = "Farinha Tipo A")
	    private String acaoOuIngrediente;

	    @Schema(description = "Valor esperado pela norma (O Plano)", example = "150.0")
	    private Double quantidadeEsperada;

	    @Schema(description = "Unidade de medida", example = "kg")
	    private String unidade;

	    // --- Getters e Setters ---
	    // (O Spring precisa deles para ler os dados do JSON)

	    public Integer getOrdem() {
	        return ordem;
	    }

	    public void setOrdem(Integer ordem) {
	        this.ordem = ordem;
	    }

	    public Double getQuantidadeReal() {
	        return quantidadeReal;
	    }

	    public void setQuantidadeReal(Double quantidadeReal) {
	        this.quantidadeReal = quantidadeReal;
	    }

	    public String getAcaoOuIngrediente() {
	        return acaoOuIngrediente;
	    }

	    public void setAcaoOuIngrediente(String acaoOuIngrediente) {
	        this.acaoOuIngrediente = acaoOuIngrediente;
	    }

	    public Double getQuantidadeEsperada() {
	        return quantidadeEsperada;
	    }

	    public void setQuantidadeEsperada(Double quantidadeEsperada) {
	        this.quantidadeEsperada = quantidadeEsperada;
	    }

	    public String getUnidade() {
	        return unidade;
	    }

	    public void setUnidade(String unidade) {
	        this.unidade = unidade;
	    }
	}
	

