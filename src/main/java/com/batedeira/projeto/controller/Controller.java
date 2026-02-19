package com.batedeira.projeto.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
//importações do Spring para a API
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.batedeira.projeto.dto.BateladaRequestDTO;
import com.batedeira.projeto.entity.Batelada;
import com.batedeira.projeto.service.BateladaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/* CONTROLLER
 * Objetivo: É a "Porta da Frente" da API. Ele é o único
 * que fala com o mundo exterior (o ESP32). Ele recebe o JSON,
 * transforma em DTO e chama o Service para fazer o trabalho.
 */

@RestController /*
				 * eu(Controller) sou o morador(API REST) que pega as cartas(DTO) da caixa de
				 * correio(RequestMapping) e entrega para o Gerente da casa(Service) resolver.
				 */
@RequestMapping("api/v1/bateladas") // eu sou a caixa de correio onde o carteiro deixa as cartas (para onde o ESP32
									// envia os dados)
@Tag(name = "bateladas", description = "Endpoint de recebimento de dados do ESP32 ")
public class Controller {

	private final BateladaService bateladaservice; // final = valor permanente

	public Controller(BateladaService bateladaservice) {
		this.bateladaservice = bateladaservice;
	}

	// método que o ESP32 vai chamar
	@PostMapping // diz ao Spring que este método responde a requisições POST
	@Operation(summary = "recebe e processa dados de uma nova batelada ")
	@ApiResponse(responseCode = "201", description = "Batelada criada com suscesso!") // resposta
	@ApiResponse(responseCode = "400", description = "Requisição inválida(JSON mal formatado") // resposta
	@ApiResponse(responseCode = "403", description = "API key inválida ou ausente(forbidden)") // resposta

	// recebe o JSON e verifica a apikey no header do JSON
	public ResponseEntity<Batelada> receberBatelada(@RequestBody BateladaRequestDTO dto,
			@RequestHeader("X-API-key") String apiKey) {

		Batelada bateladaSalva = bateladaservice.processarNovaBatelada(dto, apiKey);
		return ResponseEntity.status(HttpStatus.CREATED).body(bateladaSalva);

	}

	@GetMapping
	@Operation(summary = "Lista bateladas com paginação")
	public ResponseEntity<Page<Batelada>> listarTodas(
			@PageableDefault(page = 0, size = 10, sort = "dataInicio", direction = Sort.Direction.DESC) Pageable pageable) {

		Page<Batelada> pagina = bateladaservice.listarTodas(pageable);
		return ResponseEntity.ok(pagina);
	}

	// DELETE
	@DeleteMapping("/{id}")
	@Operation(summary = "Remove uma batelada do sistema")
	public ResponseEntity<Void> deletar(@PathVariable Long id) {
		bateladaservice.deletarBatelada(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}") // lista resumida
	@Operation(summary = "Busca os detalhes de uma batelada pelo id")
	public ResponseEntity<Batelada> buscarPorId(@PathVariable Long id) {
		Batelada batelada = bateladaservice.buscarPorId(id);
		return ResponseEntity.ok(batelada);
	}

}
