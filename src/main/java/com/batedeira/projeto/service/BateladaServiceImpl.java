package com.batedeira.projeto.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.batedeira.projeto.dto.BateladaRequestDTO;
import com.batedeira.projeto.dto.EtapaRequestDTO;
import com.batedeira.projeto.entity.Batelada;
import com.batedeira.projeto.entity.EtapaExecutada;
import com.batedeira.projeto.entity.ParametrosGlobal;
import com.batedeira.projeto.entity.enums.SensorMotor; // Importante!
import com.batedeira.projeto.entity.enums.StatusEtapa; // Assumindo que você tem esse enum
import com.batedeira.projeto.entity.enums.modoBatedeira;
import com.batedeira.projeto.entity.enums.statusBatelada;
import com.batedeira.projeto.repository.BateladaRepository;
import com.batedeira.projeto.repository.ParamRepository;
import com.batedeira.projeto.utility.Tolerancia;

@Service
public class BateladaServiceImpl implements BateladaService {



	@Value("${batedeira.projeto.api-key}")
	private String chaveCorreta;

	private final BateladaRepository bateladaRepository;
	@Autowired
	private ParamRepository paramRepository;

	public BateladaServiceImpl(BateladaRepository bateladaRepository, ParamRepository paramRepository) {
		this.paramRepository = paramRepository;
		this.bateladaRepository = bateladaRepository;
		
	}

	@Override
	@Transactional
	public Batelada processarNovaBatelada(BateladaRequestDTO dto, String apiKey) {

		if (!chaveCorreta.equals(apiKey)) {
			throw new SecurityException("API Key inválida.");
		}

		// 1. pegar os dados da batelada e guardar eles
		Batelada batelada = new Batelada();

		batelada.setDataInicio(LocalDateTime.parse(dto.getDataInicio()));
		batelada.setDataFim(LocalDateTime.parse(dto.getDataFim()));
		batelada.setModo(modoBatedeira.valueOf(dto.getModo().toUpperCase().trim()));
		batelada.setEtapasExecutadas(new ArrayList<>());
		batelada.setStatus(statusBatelada.OK);
		
		ParametrosGlobal toleranciaDef = new ParametrosGlobal();
		
		
		List<ParametrosGlobal> listaParams = paramRepository.findAll();
		
        if (!listaParams.isEmpty()) {
            
            toleranciaDef = listaParams.get(0);
        }
        
        batelada.setToleranciaAplicada(toleranciaDef.getToleranciaDef());
		
        Tolerancia calc = new Tolerancia();
        
		// 2. definir as etapas
		for (EtapaRequestDTO etapaDTO : dto.getEtapas()) {
			EtapaExecutada etapa = new EtapaExecutada();
			etapa.setBatelada(batelada);

			etapa.setOrdem(etapaDTO.getOrdem());
			etapa.setQuantidadeEsperada(etapaDTO.getQuantidadeEsperada());
			etapa.setQuantidadeReal(etapaDTO.getQuantidadeReal());
			etapa.setUnidade(etapaDTO.getUnidade());
			etapa.setIngredienteNome(SensorMotor.descobrirIngrediente(etapaDTO.getSensorAcionado()));

	        double qtdReal = etapaDTO.getQuantidadeReal();
			double qtdEsperada = etapaDTO.getQuantidadeEsperada();
			Double toleranciaEspecifica = etapaDTO.getToleranciaEspecifica();
			System.out.println(">>> Etapa " + etapaDTO.getOrdem() + " | Tol. Específica que chegou: " + toleranciaEspecifica);
			StatusEtapa vereditoE = calc.toleranciaEtapa(qtdReal, qtdEsperada, toleranciaDef, toleranciaEspecifica);
			etapa.setStatus(vereditoE);
			
			if (vereditoE == StatusEtapa.FORA_DE_TOLERANCIA) {
				batelada.setStatus(statusBatelada.FORA_DE_TOLERANCIA);
			}
			
			batelada.getEtapasExecutadas().add(etapa);
		}

		Batelada bateladaSalva = bateladaRepository.save(batelada);
		return bateladaSalva;
	}

	// --- MÉTODOS DE LEITURA ---
	@Override
	public List<Batelada> listarTodas() {
		return bateladaRepository.findAll();
	}

	@Override
	public Batelada buscarPorId(Long id) {
		return bateladaRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Batelada não encontrada com ID: " + id));
	}

	@Override
	public Page<Batelada> listarTodas(Pageable pageable) {
		return bateladaRepository.findAll(pageable);
	}

	@Override
	public void deletarBatelada(Long id) {
		if (!bateladaRepository.existsById(id)) {
			throw new RuntimeException("Batelada não encontrada.");
		}
		bateladaRepository.deleteById(id);
	}
	
	@Override
	public Page<Batelada> buscarComFiltros(String modoStr, String statusStr, String dataInicioStr, String dataFimStr, Pageable pageable) {
		
		// 1. Converte o texto do modo para o Enum correspondente
		// O .toUpperCase() é o nosso escudo: se o Front-end mandar "manual" minúsculo, o Java converte para "MANUAL" e não dá erro.
		modoBatedeira modo = (modoStr != null && !modoStr.isBlank()) 
		                     ? modoBatedeira.valueOf(modoStr.toUpperCase()) 
		                     : null;
		
		// 2. Converte o texto do status para o Enum
		statusBatelada status = (statusStr != null && !statusStr.isBlank()) 
		                        ? statusBatelada.valueOf(statusStr.toUpperCase()) 
		                        : null;
		
		// 3. Converte os textos das datas para o relógio interno do Java (LocalDateTime)
		LocalDateTime dataInicio = (dataInicioStr != null && !dataInicioStr.isBlank()) 
		                           ? LocalDateTime.parse(dataInicioStr) 
		                           : null;
		                           
		LocalDateTime dataFim = (dataFimStr != null && !dataFimStr.isBlank()) 
		                        ? LocalDateTime.parse(dataFimStr) 
		                        : null;

		// 4. Manda tudo já traduzido para o  Repository buscar
		return bateladaRepository.buscarComFiltros(modo, status, dataInicio, dataFim, pageable);
	}
}