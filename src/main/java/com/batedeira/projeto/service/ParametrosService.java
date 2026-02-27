package com.batedeira.projeto.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.batedeira.projeto.entity.ParametrosGlobal;
import com.batedeira.projeto.repository.ParamRepository;

@Service
public class ParametrosService {

	@Autowired
	private ParamRepository paramRepository;

	public double ler() {
		Optional<ParametrosGlobal> config = paramRepository.findById(1L);

		if (config.isPresent()) {
			return config.get().getToleranciaDef();
		} else
			return 5.0;
	}

	public double atualizar(double novoValor) {

		Optional<ParametrosGlobal> config = paramRepository.findById(1L);
		if (config.isPresent()) {
			ParametrosGlobal ficha = config.get();
			ficha.setToleranciaDef(novoValor);
			paramRepository.save(ficha);
		} else {
			ParametrosGlobal ficha = new ParametrosGlobal();
			ficha.setId(1L);
			ficha.setToleranciaDef(novoValor);
			paramRepository.save(ficha);
		}

		return novoValor;
	}

}
