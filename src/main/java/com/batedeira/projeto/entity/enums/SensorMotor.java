package com.batedeira.projeto.entity.enums;

public enum SensorMotor {
	
	AGUA("MOTOR_AGUA","Agua"),
	TRIGO("MOTOR_TRIGO", "Trigo"),
	COLA("MOTOR_COLA", "Cola");

	private final String codigoEsp32;
	private final String ingredienteBanco;
	
	SensorMotor(String codigoEsp32, String ingredienteBanco) {
		this.codigoEsp32 = codigoEsp32;
		this.ingredienteBanco = ingredienteBanco;
	}
	//verifica se o motor que ligou bate com o ingrediente que a receita pedia
	public static boolean validaIngrediente(String sensorRecebido, String ingredienteEsperado) { 
		
		if(sensorRecebido == null || ingredienteEsperado == null)
			return false;
		//procura quem é esse sensor
		for(SensorMotor s : values()) {
			if (s.codigoEsp32.equalsIgnoreCase(sensorRecebido)) {
				//verificamos se o ingrediente associado a ele contem o texto
				//ex: "MOTOR_TRIGO" contém "Trigo"? Sim
				return s.ingredienteBanco.toUpperCase().contains(ingredienteEsperado);
			}
		}
		
		return false;
	}
	
	
}
