package com.batedeira.projeto.service;

import com.batedeira.projeto.dto.BateladaRequestDTO;
import com.batedeira.projeto.dto.EtapaRequestDTO;
import com.batedeira.projeto.entity.Batelada;
import com.batedeira.projeto.entity.EtapaExecutada;
import com.batedeira.projeto.entity.EtapaReceita;
import com.batedeira.projeto.entity.Receita;
import com.batedeira.projeto.entity.enums.modoBatedeira;
import com.batedeira.projeto.entity.enums.statusBatelada;
import com.batedeira.projeto.repository.BateladaRepository;
import com.batedeira.projeto.repository.ReceitaRepository;
import com.batedeira.projeto.entity.enums.SensorMotor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class BateladaServiceImpl implements BateladaService {
	

    @Value("${batedeira.projeto.api-key}")
    private String chaveCorreta;

    // --- PARÂMETROS DO AUDITOR ---
    private static final Double MARGEM_ACEITACAO_RESTO = 20.0; // Kg
    private static final int HORAS_ALERTA_VALIDADE = 4;        // Horas
    private static final Double CAPACIDADE_TANQUE_KG = 500.0;  // Kg

    private final ReceitaRepository receitaRepository;
    private final BateladaRepository bateladaRepository;

    public BateladaServiceImpl(ReceitaRepository receitaRepository, BateladaRepository bateladaRepository) {
        this.receitaRepository = receitaRepository;
        this.bateladaRepository = bateladaRepository;
    }

    @Override
    @Transactional
    public String processarNovaBatelada(BateladaRequestDTO dto, String apiKey) {
        
        // 1. Segurança
        if (!chaveCorreta.equals(apiKey)) {
            throw new SecurityException("API Key inválida.");
        }

        // 2. Criação do Relatório
        Batelada batelada = new Batelada();
        batelada.setModo(modoBatedeira.valueOf(dto.getModo()));
        
        LocalDateTime inicioReal = LocalDateTime.parse(dto.getDataInicio(), DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime fimReal = LocalDateTime.parse(dto.getDataFim(), DateTimeFormatter.ISO_DATE_TIME);
        
        batelada.setDataInicio(inicioReal);
        batelada.setDataFim(fimReal);
        batelada.setStatus(statusBatelada.OK); 

        // --- 3. O AUDITOR SILENCIOSO ---
        Double pesoInicial = dto.getSobraAnterior();
        if (pesoInicial == null) pesoInicial = 0.0;
        batelada.setSobraAnterior(pesoInicial);

        // Busca a ficha da última batelada para comparar tempos
        Optional<Batelada> ultimaBateladaOpt = bateladaRepository.findTopByOrderByDataFimDesc();

        if (ultimaBateladaOpt.isPresent()) {
            Batelada ultima = ultimaBateladaOpt.get();
            
            long minutosParado = ChronoUnit.MINUTES.between(ultima.getDataFim(), inicioReal);
            batelada.setMinutosOciosoAnterior(minutosParado);

            if (pesoInicial <= MARGEM_ACEITACAO_RESTO) {
                // Tanque Limpo
                batelada.setIniciouComTanqueLimpo(true);
                System.out.println(">>> AUDITOR: Tanque Limpo. Sobra: " + pesoInicial);
            } else {
                // Tanque Sujo / Retomada
                batelada.setIniciouComTanqueLimpo(false);
                long horasParado = minutosParado / 60;
                
                System.out.println(">>> AUDITOR: Produto no tanque (" + pesoInicial + "kg). Parado há " + minutosParado + " min.");

                if (horasParado >= HORAS_ALERTA_VALIDADE) {
                    batelada.setErroMensagem("ALERTA: Sobra parada há " + horasParado + "h.");
                } else {
                    batelada.setErroMensagem("Nota: Retomada de processo.");
                }
            }
        } else {
            // Primeira vez rodando o sistema
            batelada.setMinutosOciosoAnterior(0L);
            batelada.setIniciouComTanqueLimpo(true);
            System.out.println(">>> AUDITOR: Primeira batelada do sistema. Tanque assumido como LIMPO.");
        }

        // 4. Checagem de Transbordo (Preliminar)
        // Nota: A validação exata depende do cálculo real abaixo, mas aqui fazemos uma estimativa
        // baseada no que o ESP32 mandou como "Real" (mesmo se for acumulado, ajustamos depois).

        // 5. Gestão de Receita (Sync ou Stub)
        Receita receitaEncontrada = null;
        if (modoBatedeira.AUTOMATICO.toString().equals(dto.getModo()) && dto.getReceitaId() != null) {
            Optional<Receita> busca = receitaRepository.findById(dto.getReceitaId());
            if (busca.isPresent()) {
                receitaEncontrada = busca.get();
                sincronizarReceitaSeNecessario(receitaEncontrada, dto);
            } else {
                receitaEncontrada = criarNovaReceitaStub(dto);
            }
        }
        batelada.setReceita(receitaEncontrada);

        // --- 6. SALVA ETAPAS (COM A LÓGICA DE SUBTRAÇÃO INTELIGENTE) ---
        
        // Começamos a conta a partir do que já estava no tanque
        double leituraAnterior = batelada.getSobraAnterior();

        for (EtapaRequestDTO etapaDTO : dto.getEtapas()) {
        	
        	
        	// === MELHORIA 1: FILTRO DE RUÍDO (O "Ignora Formiga") ===
            // Se o peso for muito pequeno (ex: vibração do motor ou pingo de 0.2kg),
            // a gente ignora para não sujar o relatório.
            if (etapaDTO.getQuantidadeReal() != null && etapaDTO.getQuantidadeReal() < 0.5) {
                System.out.println(">>> ALERTA: Leitura de ruído ignorada: " + etapaDTO.getQuantidadeReal() + "kg");
                continue; // Pula para a próxima etapa, não salva essa!
            }
            
            
            // === MELHORIA 2: VALIDACAO DE SEGURANÇA (O "Xerife") ===
            // Só validamos se tivermos uma Receita carregada (Modo Automático) 
            // e se o ESP32 mandou o nome do sensor.
            if (receitaEncontrada != null && etapaDTO.getSensorAcionado() != null) {
                
                // 1. Descobrimos o que a Receita esperava para essa ordem (ex: Passo 1)
                Optional<EtapaReceita> etapaEsperadaOpt = receitaEncontrada.getEtapas().stream()
                        .filter(e -> e.getOrdem() == etapaDTO.getOrdem())
                        .findFirst();

                if (etapaEsperadaOpt.isPresent()) {
                    String ingredienteEsperado = etapaEsperadaOpt.get().getAcaoOuIngrediente(); // Ex: "Agua"
                    String sensorRecebido = etapaDTO.getSensorAcionado();                       // Ex: "MOTOR_TRIGO" (Ops!)

                    // 2. Chamamos nosso Enum para julgar
                    boolean isCorreto = SensorMotor.validaIngrediente(sensorRecebido, ingredienteEsperado);

                    if (!isCorreto) {
                        // CRIME DETECTADO! O motor errado ligou.
                        batelada.setStatus(statusBatelada.ERRO);
                        String msgErro = "ERRO GRAVE: Troca de ingredientes! Passo " + etapaDTO.getOrdem() + 
                                         " esperava '" + ingredienteEsperado + 
                                         "' mas sensor acusou '" + sensorRecebido + "'";
                        
                        batelada.setErroMensagem(msgErro);
                        System.out.println("🚨 " + msgErro);
                    }
                }
            }
        	
        	
            EtapaExecutada executada = new EtapaExecutada();
            executada.setOrdem(etapaDTO.getOrdem());
            
            // O valor que vem do ESP32 (Pode ser acumulado ou não)
            double valorLidoPeloSensor = etapaDTO.getQuantidadeReal();
            double quantidadeRealDoIngrediente;

            // A Mágica: Descobre se é acumulativo ou absoluto
            if (valorLidoPeloSensor >= leituraAnterior) {
                // Se o valor subiu, é acumulativo: o ingrediente é a diferença
                quantidadeRealDoIngrediente = valorLidoPeloSensor - leituraAnterior;
            } else {
                // Se o valor desceu, houve uma tara/reset: o ingrediente é o valor cheio
                quantidadeRealDoIngrediente = valorLidoPeloSensor;
            }
            
            quantidadeRealDoIngrediente = Math.round(quantidadeRealDoIngrediente * 100.0) / 100.0;
            // Salva o valor REAL do ingrediente (descontado)
            executada.setQuantidadeReal(quantidadeRealDoIngrediente);
            
            // Atualiza a leitura anterior para a próxima etapa do loop
            leituraAnterior = valorLidoPeloSensor;
            
            batelada.adicionarEtapaExecutada(executada);
        }
        
        // Verificação final de transbordo (agora com os valores corrigidos se necessário)
        double totalAdicionado = batelada.getEtapasExecutadas().stream()
                                         .mapToDouble(EtapaExecutada::getQuantidadeReal).sum();
        
        if ((totalAdicionado + pesoInicial) > CAPACIDADE_TANQUE_KG) {
             batelada.setStatus(statusBatelada.ERRO); 
             batelada.setErroMensagem("PERIGO: Risco de Transbordo! Total no tanque > " + CAPACIDADE_TANQUE_KG);
        }

        // 7. Salva no Banco e retorna ID
        Batelada bateladaSalva = bateladaRepository.save(batelada);
        System.out.println(">>> ServiceImpl: SUCESSO! Batelada Salva com ID: " + bateladaSalva.getId());
        
        return bateladaSalva.getId().toString();
    }
    
    // --- IMPLEMENTAÇÃO DOS MÉTODOS DE LEITURA (GET) ---
    @Override
    public List<Batelada> listarTodas() {
        return bateladaRepository.findAll();
    }

    @Override
    public Batelada buscarPorId(Long id) {
        return bateladaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batelada não encontrada com ID: " + id));
    }

    // --- MÉTODOS AUXILIARES (Receita) ---

    private Receita criarNovaReceitaStub(BateladaRequestDTO dto) {
        Receita novaReceita = new Receita();
        novaReceita.setId(dto.getReceitaId());
        novaReceita.setNomeAmigavel("Receita: #" + dto.getReceitaId() + " (Auto)");
        novaReceita.setDataDescoberta(LocalDateTime.now());
        novaReceita.setAtiva(true);

        for (EtapaRequestDTO etapaDto : dto.getEtapas()) {
            EtapaReceita novaEtapa = new EtapaReceita();
            novaEtapa.setOrdem(etapaDto.getOrdem());
            novaEtapa.setAcaoOuIngrediente(etapaDto.getAcaoOuIngrediente());
            novaEtapa.setQuantidadeEsperada(etapaDto.getQuantidadeEsperada());
            novaEtapa.setUnidade(etapaDto.getUnidade());
            novaEtapa.setToleranciaPercentual(null);
            novaReceita.addEtapa(novaEtapa);
            
        }
        return receitaRepository.save(novaReceita);
    }

    private void sincronizarReceitaSeNecessario(Receita receitaBanco, BateladaRequestDTO dto) {
        boolean houveMudanca = false;
        for (EtapaRequestDTO etapaDto : dto.getEtapas()) {
            Optional<EtapaReceita> etapaBancoOpt = receitaBanco.getEtapas().stream()
                    .filter(e -> e.getOrdem() == etapaDto.getOrdem())
                    .findFirst();

            if (etapaBancoOpt.isPresent()) {
                EtapaReceita etapaBanco = etapaBancoOpt.get();
                // Verifica mudanças no Plano (Receita)
                boolean valorMudou = Math.abs(etapaBanco.getQuantidadeEsperada() - etapaDto.getQuantidadeEsperada()) > 0.001;
                boolean descricaoMudou = !etapaBanco.getAcaoOuIngrediente().equals(etapaDto.getAcaoOuIngrediente());

                if (valorMudou || descricaoMudou) {
                    etapaBanco.setQuantidadeEsperada(etapaDto.getQuantidadeEsperada());
                    etapaBanco.setAcaoOuIngrediente(etapaDto.getAcaoOuIngrediente());
                    etapaBanco.setToleranciaPercentual(null);
                    houveMudanca = true;
                }
            }
        }
        if (houveMudanca) receitaRepository.save(receitaBanco);
    }
    
    @Override
    public Page<Batelada> listarTodas(Pageable pageable) {
        // O findAll do repositório já sabe paginar!
        return bateladaRepository.findAll(pageable);
    }

    @Override
    public void deletarBatelada(Long id) {
        if (!bateladaRepository.existsById(id)) {
            throw new RuntimeException("Batelada não encontrada para exclusão.");
        }
        bateladaRepository.deleteById(id);
    }
}