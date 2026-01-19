package com.batedeira.projeto.controller;

//informações do contrato da API que foram definidos.
import com.batedeira.projeto.dto.BateladaRequestDTO;
//import da interface
import com.batedeira.projeto.service.BateladaService;

//importações das anotações do swagger(openAPI) para documentação
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

//importações do Spring para a API
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;




/* CONTROLLER
 * Objetivo: É a "Porta da Frente" da API. Ele é o único
 * que fala com o mundo exterior (o ESP32). Ele recebe o JSON,
 * transforma em DTO e chama o Service para fazer o trabalho.
 */


@RestController /* eu(Controller) sou o morador(API REST) que pega as cartas(DTO) da caixa de correio(RequestMapping) e
 *entrega para o Gerente da casa(Service) resolver.*/
@RequestMapping("api/v1/bateladas") //eu sou a caixa de correio onde o carteiro deixa as cartas (para onde o ESP32 envia os dados)
@Tag(name = "bateladas", description = "Endpoint de recebimento de dados do ESP32 ") //Agrupa no swagger
public class Controller {



	/* injeção de dependencia
	 * Novo pedido: um novo bateladaService(unico que sabe o que fazer com as cartas do correio(DTO's))
	 * que se chame "bateladaservice" e seja privado(private) e permanente(final)
	 */
	private final BateladaService bateladaservice;

	
	/**
     * (5) O "Contrato de Contratação" (O Construtor).
     * Diz ao  Spring: "Para me contratar (criar), você é OBRIGADO
     * a me entregar um Chef (um BateladaService) que eu penduro no meu 'gancho'."
     * O Spring obedece e "injeta" o BateladaServiceImpl aqui.
     */

	public Controller(BateladaService bateladaservice) {

		this.bateladaservice = bateladaservice;
	}





	/*
	 * Método que o ESP32 vai chamar
	 */
	@PostMapping // diz ao Spring que este método responde a requisições POST
	@Operation(summary = "recebe e processa dados de uma nova batelada ")
	@ApiResponse(responseCode = "201", description = "Batelada criada com suscesso!")//resposta
	@ApiResponse(responseCode = "400", description = "Requisição inválida(JSON mal formatado") //resposta
	@ApiResponse(responseCode = "403", description = "API key inválida ou ausente(forbidden)") //resposta


	public ResponseEntity <String> receberBatelada (

			/* "Pegue a 'Carta' (o texto JSON) que o 'Carteiro' (ESP32) acabou de entregar no 'Corpo' (Body) da requisição...
			 * ...e use-a para preencher o 'Formulário' (a classe BateladaRequestDTO).
			 *  Me entregue esse formulário preenchido com o apelido de dto." */
			@RequestBody BateladaRequestDTO dto,

			@RequestHeader ("X-API-key") String apiKey) { /*"Quando um pedido chegar, olhe no 'Cabeçalho' (Header) dele. Procure 
														   *por uma etiqueta chamada X-API-Key.Pegue o valor que está escrito nela
			 											   * e me entregue na variável apiKey."
			 											   */


		// Por enquanto, vamos só simular a chamada:
		System.out.println("Controller: Recebi o ID da Receita: " + dto.getReceitaId());
		System.out.println("Controller: API Key recebida: " + apiKey);

		//pega o dto e a apikey e envia para o Service
		String idDaNovaBatelada = bateladaservice.processarNovaBatelada(dto, apiKey);

		//Responde ao ESP32 que deu tudo certo (HTTP 201 Created)
		return ResponseEntity.status(HttpStatus.CREATED).body("Batelada recebida com id: " + idDaNovaBatelada);



	}	
}
