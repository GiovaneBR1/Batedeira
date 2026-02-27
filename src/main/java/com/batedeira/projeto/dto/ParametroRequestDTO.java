package com.batedeira.projeto.dto;

public class ParametroRequestDTO {
	
	 public Double novoValorTolerancia;
	 
	 public ParametroRequestDTO() {
		}

	 public ParametroRequestDTO(Double novoValorTolerancia) {
		this.novoValorTolerancia = novoValorTolerancia;
	 }

	 public Double getNovoValorTolerancia() {
		 return novoValorTolerancia;
	 }

	

	 public void setNovoValorTolerancia(Double novoValorTolerancia) {
		 this.novoValorTolerancia = novoValorTolerancia;
	 }

}
