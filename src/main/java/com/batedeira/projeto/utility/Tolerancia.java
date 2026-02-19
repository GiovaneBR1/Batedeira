package com.batedeira.projeto.utility;

import com.batedeira.projeto.entity.ParametrosGlobal;
import com.batedeira.projeto.entity.enums.StatusEtapa;
import com.batedeira.projeto.entity.enums.statusBatelada;

public class Tolerancia {
	
	public StatusEtapa toleranciaEtapa(double qtdReal, double qtdEsperada,double toleranciDef)  {
		ParametrosGlobal porcentagem = new ParametrosGlobal();
		double porcentagemAceitação = porcentagem.getToleranciaDef();

		if (qtdEsperada <= 0 && qtdReal <= 0) {
			return StatusEtapa.ERRO;
		}
		
		
		double diferenca = Math.abs(qtdReal - qtdEsperada);
		double toleranciaPercentual = (diferenca / qtdEsperada) * 100;

		if (toleranciaPercentual <= porcentagemAceitação) {
			return StatusEtapa.OK;

		} else

			return StatusEtapa.FORA_DE_TOLERANCIA;
	}

}
