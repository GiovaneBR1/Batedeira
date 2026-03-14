package com.batedeira.projeto.service;

import com.batedeira.projeto.entity.ParametrosGlobal;
import com.batedeira.projeto.repository.ParamRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class AlertaManutencaoService {

    private final ParamRepository paramRepository;

    // Injetamos os parâmetros para podermos ler a data
    public AlertaManutencaoService(ParamRepository paramRepository) {
        this.paramRepository = paramRepository;
    }

    /**
     * O atributo "cron" é a linguagem universal dos relógios de servidores.
     * "0 0 8 * * *" significa: Segundos(0) Minutos(0) Horas(8) Dia(*) Mês(*) Ano(*)
     * Ou seja, rodaria todos os dias às 8h00 da manhã.
     * * MAS, para nós testarmos agora sem esperar até amanhã, 
     * eu coloquei "0 * * * * *", que significa: "Rode no segundo ZERO de TODO minuto!"
     */
    @Scheduled(cron = "0 * * * * *")
    public void verificarManutencaoDiaria() {
        
        System.out.println("⏰ [VIGIA] Acordei! Verificando validade da manutenção...");

        Optional<ParametrosGlobal> configOpt = paramRepository.findById(1L);

        if (configOpt.isPresent()) {
            ParametrosGlobal config = configOpt.get();
            LocalDateTime ultimaManutencao = config.getDataUltimaManutencao();

            if (ultimaManutencao != null) {
                // A conta matemática: Hoje menos a data da última manutenção
                long diasPassados = ChronoUnit.DAYS.between(ultimaManutencao, LocalDateTime.now());

                if (diasPassados >= 30) {
                    System.out.println("🚨 ALERTA CRÍTICO: Máquina sem manutenção há " + diasPassados + " dias! 🚨");
                    // Aqui, num futuro, podes fazer o código enviar um email ao gerente, 
                    // travar a máquina mudando um status no banco, etc.
                } else {
                    System.out.println("✅ Máquina em dia. Faltam " + (30 - diasPassados) + " dias para a próxima revisão.");
                }
            } else {
                System.out.println("⚠️ ATENÇÃO: O Admin ainda não registou a data da primeira manutenção!");
            }
        }
    }
}