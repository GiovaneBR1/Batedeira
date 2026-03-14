package com.batedeira.projeto.utility;

import com.batedeira.projeto.entity.ParametrosGlobal;
import com.batedeira.projeto.entity.enums.StatusEtapa;

public class Tolerancia {

	public StatusEtapa toleranciaEtapa(double qtdReal, double qtdEsperada, ParametrosGlobal toleranciaDef,
			Double toleranciaEspecifica) {

		//Double porcentagemAceitação = toleranciaDef.getToleranciaDef();

		if (qtdEsperada <= 0 && qtdReal <= 0) {
			return StatusEtapa.ERRO;
		}
		double porcentagemPermitida;
		if (toleranciaEspecifica != null) {
			porcentagemPermitida = toleranciaEspecifica; // Usa a específica do ingrediente
		} else {
			// Se não tem específica, puxa a global (ou 5.0 se der ruim)
			porcentagemPermitida = (toleranciaDef != null && toleranciaDef.getToleranciaDef() != null)
					? toleranciaDef.getToleranciaDef() : 5.0; //essa estrutura é chamada de operador ternário e faz a mesma função do if-else
		}
		double diferenca = Math.abs(qtdReal - qtdEsperada);
		double toleranciaPercentual = (diferenca / qtdEsperada) * 100;

		if (toleranciaPercentual <= porcentagemPermitida) {
			return StatusEtapa.OK;

		} else

			return StatusEtapa.FORA_DE_TOLERANCIA;
	}

}
