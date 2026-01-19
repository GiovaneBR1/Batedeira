package com.batedeira.projeto.service;

// --- Imports Limpos ---
import com.batedeira.projeto.dto.BateladaRequestDTO;
import com.batedeira.projeto.dto.EtapaRequestDTO;
import com.batedeira.projeto.entity.Batelada;
import com.batedeira.projeto.entity.EtapaExecutada;
import com.batedeira.projeto.entity.EtapaReceita;
import com.batedeira.projeto.entity.Receita;
// ATENÇÃO: Confirme se os nomes dos seus Enums estão com letra Maiúscula ou minúscula no arquivo original
import com.batedeira.projeto.entity.enums.modoBatedeira; 
import com.batedeira.projeto.entity.enums.statusBatelada;
import com.batedeira.projeto.repository.BateladaRepository;
import com.batedeira.projeto.repository.ReceitaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class BateladaServiceImpl implements BateladaService {

    @Value("${batedeira.projeto.api-key}")
    private String chaveCorreta;
    
    private static final Double MARGEM_ACEITACAO_RESTO = 20.0;
    private static final int HORAS_ALERTA_VALIDADE = 4;
    private static final Double  CAPACIDADE_TANQUE_KG = 500.0;

    private final ReceitaRepository receitaRepository;
    private final BateladaRepository bateladaRepository;

    public BateladaServiceImpl(ReceitaRepository receitaRepository, BateladaRepository bateladaRepository) {
        this.receitaRepository = receitaRepository;
        this.bateladaRepository = bateladaRepository;
    }

    @Override
    @Transactional // Garante a segurança dos dados
    public String processarNovaBatelada(BateladaRequestDTO dto, String apiKey) {
        
        // 1. Validação da API Key
        if (!chaveCorreta.equals(apiKey)) {
            throw new SecurityException("API Key inválida ou ausente.");
        }
        
        //preparando a Bateladaa
        Batelada batelada = new Batelada();
        batelada.setModo(modoBatedeira.valueOf(dto.getModo())); //seta o modo
        
        //horario que veio do CLP
        LocalDateTime inicioReal = LocalDateTime.parse(dto.getDataInicio(), DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime fimReal = LocalDateTime.parse(dto.getDataFim(), DateTimeFormatter.ISO_DATE_TIME);
        
        batelada.setDataInicio(inicioReal);
        batelada.setDataFim(fimReal);
        batelada.setStatus(statusBatelada.OK);

        // 2. Lógica da Receita (O Plano)
        Receita receitaEncontrada = null; // Começa nulo (Assume Manual)

        // Se for AUTOMATICO, resolvemos a receita. Se for MANUAL, pulamos este bloco.
        if (modoBatedeira.AUTOMATICO.toString().equals(dto.getModo())) {

            if (dto.getReceitaId() == null) {
                throw new IllegalArgumentException("ID da receita é obrigatório no modo automático");
            }

            Optional<Receita> busca = receitaRepository.findById(dto.getReceitaId());
            
            if (busca.isPresent()) {
                // ACHOU: Usa a existente e verifica sincronia
                receitaEncontrada = busca.get();
                sincronizarReceitaSeNecessario(receitaEncontrada, dto);
            } else {
                // NÃO ACHOU: Cria uma nova (Stub)
                receitaEncontrada = criarNovaReceitaStub(dto);
            }
        }

        // 3. Montagem da Batelada (O Fato)
        // (ISSO AGORA ESTÁ FORA DO IF DO AUTOMÁTICO - FUNCIONA P/ MANUAL TAMBÉM!)
        
        
        
        // Converte String para Enum (Trata maiúsculas/minúsculas por segurança)
        batelada.setModo(modoBatedeira.valueOf(dto.getModo())); 
        
        batelada.setDataInicio(LocalDateTime.parse(dto.getDataInicio(), DateTimeFormatter.ISO_DATE_TIME));
        batelada.setDataFim(LocalDateTime.parse(dto.getDataFim(), DateTimeFormatter.ISO_DATE_TIME));
        batelada.setStatus(statusBatelada.OK);

        // Conecta a receita (pode ser null se for Manual)
        batelada.setReceita(receitaEncontrada);

        // 4. Loop das Etapas Executadas
        for (EtapaRequestDTO etapaDTO : dto.getEtapas()) {
            EtapaExecutada executada = new EtapaExecutada();
            executada.setOrdem(etapaDTO.getOrdem());
            executada.setQuantidadeReal(etapaDTO.getQuantidadeReal());
            
            // Adiciona no bolso da Batelada
            batelada.adicionarEtapaExecutada(executada);
        }

        // 5. Salva tudo e Retorna o ID
        Batelada bateladaSalva = bateladaRepository.save(batelada);
        System.out.println(">>> ServiceImpl: SUCESSO! Batelada Salva com ID: " + bateladaSalva.getId());

        return bateladaSalva.getId().toString();
    }

    // --- Métodos Ajudantes ---

    private Receita criarNovaReceitaStub(BateladaRequestDTO dto) {
        Receita novaReceita = new Receita();
        novaReceita.setId(dto.getReceitaId());
        novaReceita.setNomeAmigavel("Receita: #" + dto.getReceitaId() + " (Auto-detectada)");
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
            
            // Procura a etapa correspondente no banco
            Optional<EtapaReceita> etapaBancoOpt = receitaBanco.getEtapas().stream()
                    .filter(e -> e.getOrdem() == etapaDto.getOrdem())
                    .findFirst();

            if (etapaBancoOpt.isPresent()) {
                EtapaReceita etapaBanco = etapaBancoOpt.get();

                // Compara valores (Double vs Double)
                boolean valorMudou = Math.abs(etapaBanco.getQuantidadeEsperada() - etapaDto.getQuantidadeEsperada()) > 0.001;
                
                // Compara descrição (String vs String)
                boolean descricaoMudou = !etapaBanco.getAcaoOuIngrediente().equals(etapaDto.getAcaoOuIngrediente());

                if (valorMudou || descricaoMudou) {
                    System.out.println(">>> ServiceImpl: Mudança detectada na etapa " + etapaBanco.getOrdem() + ". Atualizando...");

                    etapaBanco.setQuantidadeEsperada(etapaDto.getQuantidadeEsperada());
                    etapaBanco.setAcaoOuIngrediente(etapaDto.getAcaoOuIngrediente());
                    etapaBanco.setUnidade(etapaDto.getUnidade());
                    
                    // Reseta tolerância por segurança
                    etapaBanco.setToleranciaPercentual(null);

                    houveMudanca = true;
                }
            }
        }

        // CORREÇÃO: Faltava este bloco! Se mudou, tem que salvar!
        if (houveMudanca) {
            receitaRepository.save(receitaBanco);
            System.out.println(">>> ServiceImpl: Receita atualizada no banco com sucesso.");
        }
    }
}