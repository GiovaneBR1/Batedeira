package com.batedeira.projeto.service;

import com.batedeira.projeto.dto.BateladaRequestDTO;
import com.batedeira.projeto.dto.EtapaRequestDTO;
import com.batedeira.projeto.entity.Batelada;
import com.batedeira.projeto.entity.EtapaExecutada;
import com.batedeira.projeto.entity.enums.SensorMotor; // Importante!
import com.batedeira.projeto.entity.enums.modoBatedeira;
import com.batedeira.projeto.entity.enums.statusBatelada;
import com.batedeira.projeto.entity.enums.StatusEtapa; // Assumindo que você tem esse enum
import com.batedeira.projeto.repository.BateladaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class BateladaServiceImpl implements BateladaService {


	@Value("${batedeira.projeto.api-key}")
    private String chaveCorreta;

    // --- PARÂMETROS ---
    private static final Double MARGEM_ACEITACAO_RESTO = 20.0; 
    private static final int HORAS_ALERTA_VALIDADE = 4;        
    private static final Double TOLERANCIA_PADRAO = 0.05; // 5% de tolerância

    private final BateladaRepository bateladaRepository;
    // O ReceitaRepository nem é mais usado aqui, pode remover se quiser limpar mais

    public BateladaServiceImpl(BateladaRepository bateladaRepository) {
        this.bateladaRepository = bateladaRepository;
    }

    @Override
    @Transactional
    public Batelada processarNovaBatelada(BateladaRequestDTO dto, String apiKey) {
        
        // 1. Segurança
        if (!chaveCorreta.equals(apiKey)) {
            throw new SecurityException("API Key inválida.");
        }

        // 2. Cabeçalho da Batelada
        Batelada batelada = new Batelada();
        batelada.setModo(modoBatedeira.valueOf(dto.getModo())); // Cuidado: certifique-se que o texto do JSON bate com o Enum
        
        LocalDateTime inicioReal = LocalDateTime.parse(dto.getDataInicio(), DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime fimReal = LocalDateTime.parse(dto.getDataFim(), DateTimeFormatter.ISO_DATE_TIME);
        
        batelada.setDataInicio(inicioReal);
        batelada.setDataFim(fimReal);
        batelada.setStatus(statusBatelada.OK); // Começa otimista

        // --- 3. O AUDITOR SILENCIOSO (Tanque Sujo/Limpo) ---
        Double pesoInicial = dto.getSobraAnterior();
        if (pesoInicial == null) pesoInicial = 0.0;
        batelada.setSobraAnterior(pesoInicial);

        Optional<Batelada> ultimaBateladaOpt = bateladaRepository.findTopByOrderByDataFimDesc();

        if (ultimaBateladaOpt.isPresent()) {
            Batelada ultima = ultimaBateladaOpt.get();
            long minutosParado = ChronoUnit.MINUTES.between(ultima.getDataFim(), inicioReal);
            batelada.setMinutosOciosoAnterior(minutosParado);

            if (pesoInicial <= MARGEM_ACEITACAO_RESTO) {
                batelada.setIniciouComTanqueLimpo(true);
            } else {
                // CASO DE ERRO: Tanque Sujo
                batelada.setIniciouComTanqueLimpo(false);
                batelada.setStatus(statusBatelada.ERRO); // Já marca como ERRO logo de cara
                
                // 1. Cria a mensagem reclamando da quantidade
                StringBuilder aviso = new StringBuilder();
                aviso.append("TANQUE SUJO: ").append(pesoInicial).append("kg encontrados (Max: ").append(MARGEM_ACEITACAO_RESTO).append("kg)");

                // 2. Verifica a validade (Tempo Parado)
                long horasParado = minutosParado / 60;
                
                if (horasParado >= HORAS_ALERTA_VALIDADE) {
                    // Se também estiver vencido, ADICIONA essa informação na mensagem
                    aviso.append(" | ALERTA: Produto parado há ").append(horasParado).append("h");
                }
                
                // 3. Grava a mensagem completa
                batelada.setErroMensagem(aviso.toString());
            
            }
        } else {
            batelada.setMinutosOciosoAnterior(0L);
            batelada.setIniciouComTanqueLimpo(true);
        }

        
        for (EtapaRequestDTO etapaDTO : dto.getEtapas()) {
        	
        

            // A. Filtro de Ruído
            if (etapaDTO.getQuantidadeReal() != null && etapaDTO.getQuantidadeReal() < 0.5) {
                System.out.println(">>> Ignorando ruído de sensor: " + etapaDTO.getQuantidadeReal() + "kg");
            	if (batelada.getEtapasExecutadas().isEmpty()) {
            	    // Se o filtro removeu tudo (era só ruído), não salva nada!
            	    return null; 
            	    }
                continue; 
            }

            EtapaExecutada etapa = new EtapaExecutada();
            
            // B. Identificação Inteligente (Via Enum SensorMotor)
            // Aqui usamos o método estático que criamos no SensorMotor
            String nomeIngrediente = SensorMotor.descobrirIngrediente(etapaDTO.getSensorAcionado());
            etapa.setIngredienteNome(nomeIngrediente);
            
            // C. Valores vindos do JSON (Fonte da Verdade)
            Double esperado = etapaDTO.getQuantidadeEsperada();
            Double real = etapaDTO.getQuantidadeReal();
            
            etapa.setQuantidadeEsperada(esperado);
            etapa.setQuantidadeReal(real);
            etapa.setUnidade("KG"); 
            etapa.setOrdem(etapaDTO.getOrdem());

            // D. Cálculo de Tolerância
            if (esperado != null && esperado > 0) {
                double diferenca = Math.abs(real - esperado);
                double limiteAceitavel = esperado * TOLERANCIA_PADRAO; // 5%

                if (diferenca > limiteAceitavel) {
                    etapa.setStatus(StatusEtapa.ERRO); // Crie este enum se não tiver, ou use String "ERRO"
                    batelada.setStatus(statusBatelada.ERRO); // Marca a batelada pai como erro
                    
                    // Adiciona mensagem de erro sem apagar a anterior (concatenação)
                    String msgAtual = batelada.getErroMensagem() == null ? "" : batelada.getErroMensagem() + " | ";
                    batelada.setErroMensagem(msgAtual + "Erro em " + nomeIngrediente + ": Esperado " + esperado + ", Real " + real);
                } else {
                    etapa.setStatus(StatusEtapa.OK);
                }
            } else {
                // Se não veio valor esperado (ex: adição manual), assumimos OK
                etapa.setStatus(StatusEtapa.OK);
            }

            // E. Vínculo (Pai <-> Filho)
            etapa.setBatelada(batelada);
            batelada.getEtapasExecutadas().add(etapa);
            
            
        }

        
        
        // 5. Salvar Tudo
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