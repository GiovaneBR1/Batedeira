package com.batedeira.projeto.service;

import java.util.List;

import com.batedeira.projeto.dto.BateladaRequestDTO;
import com.batedeira.projeto.entity.Batelada;


/*É o "Contrato" Oficial que define O QUE a classe 
 * BateladaServiceImpl DEVE SABER fazer */
public interface BateladaService {
	
	/* 1- String -> o QUE deve ser retornado. Nesse caso, o ID da batelada)
	 * 2- processarNovaBatelada -> o método que o controller vai chamar
	 * 3- (BateladaRequestDTO dto, String apiKey) -> Os parâmetros que o controller deve entregar
	 * ao BateladaServiceImpl(quem realmente faz a lógica).  
	 */
	String processarNovaBatelada (BateladaRequestDTO dto, String apiKey);
	
	List<Batelada> listarTodas(); 
	Batelada buscarPorId(Long id);
	

}
