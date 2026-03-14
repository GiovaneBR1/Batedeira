package com.batedeira.projeto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.batedeira.projeto.dto.ParametroRequestDTO;
import com.batedeira.projeto.service.ParametrosService;

import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/v1/config")
public class ParametrosController {

	public ParametrosController(ParametrosService service) {

	}

	@Autowired
	ParametrosService service;

	@GetMapping("/ler")
	public ResponseEntity<Double> ler() {
		double novoValor = service.ler();
		return ResponseEntity.ok(novoValor);
	}

	@PostMapping("/atualizar")
	public ResponseEntity<Double> atualizar(@RequestBody ParametroRequestDTO dto) {
		
		Double novoValor = dto.getNovoValorTolerancia();
		if (novoValor == null || novoValor == 0) {
			System.out.println("Não permitido definir tolerancia zero");
			novoValor = 5.0; 
		    return ResponseEntity.badRequest().build(); 
		}
		service.atualizar(novoValor);
		return ResponseEntity.ok(novoValor);

	}
}
