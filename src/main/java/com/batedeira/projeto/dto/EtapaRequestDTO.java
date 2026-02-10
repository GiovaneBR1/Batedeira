package com.batedeira.projeto.dto;

 

public class EtapaRequestDTO {

    private Integer ordem;
    
    // O código do sensor que vem do ESP32 (ex: "MOTOR_AGUA")
    // Essencial para o Service descobrir o nome do ingrediente
    private String sensorAcionado; 

    // O Setpoint (O que o operador pediu no CLP)
    // Essencial para calcularmos se houve erro ou não
    private Double quantidadeEsperada;

    // O Valor Real (O que a balança pesou)
    private Double quantidadeReal;

    // Unidade de medida (opcional, pode deixar fixo no Java se for sempre KG)
    private String unidade;

	public EtapaRequestDTO(Integer ordem, String sensorAcionado, Double quantidadeEsperada, Double quantidadeReal,
			String unidade) {
		super();
		this.ordem = ordem;
		this.sensorAcionado = sensorAcionado;
		this.quantidadeEsperada = quantidadeEsperada;
		this.quantidadeReal = quantidadeReal;
		this.unidade = unidade;
	}

	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public String getSensorAcionado() {
		return sensorAcionado;
	}

	public void setSensorAcionado(String sensorAcionado) {
		this.sensorAcionado = sensorAcionado;
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
}