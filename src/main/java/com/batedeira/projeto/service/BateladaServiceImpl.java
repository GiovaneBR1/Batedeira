package com.batedeira.projeto.service;

import com.batedeira.projeto.controller.Controller;
import com.batedeira.projeto.dto.BateladaRequestDTO;
import com.batedeira.projeto.dto.EtapaRequestDTO;
import com.batedeira.projeto.entity.Batelada;
import com.batedeira.projeto.entity.EtapaExecutada;
import com.batedeira.projeto.entity.ParametrosGlobal;
import com.batedeira.projeto.entity.enums.SensorMotor; // Importante!
import com.batedeira.projeto.entity.enums.modoBatedeira;
import com.batedeira.projeto.entity.enums.statusBatelada;
import com.batedeira.projeto.entity.enums.StatusEtapa; // Assumindo que você tem esse enum
import com.batedeira.projeto.repository.BateladaRepository;
import com.batedeira.projeto.repository.ParamRepository;
import com.batedeira.projeto.utility.Tolerancia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
		batelada.setSobraAnterior(dto.getSobraAnterior());
		
		batelada.setEtapasExecutadas(new ArrayList<>());
		batelada.setStatus(statusBatelada.OK);
		
		double toleranciaDef = 5.0;
		
		List<ParametrosGlobal> listaParams = paramRepository.findAll();
        if (!listaParams.isEmpty()) {
            
            toleranciaDef = listaParams.get(0).getToleranciaDef();
        }
        
        batelada.setToleranciaAplicada(toleranciaDef);
		
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
			StatusEtapa vereditoE = calc.toleranciaEtapa(qtdReal, qtdEsperada,toleranciaDef);
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
}