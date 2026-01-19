package com.batedeira.projeto.repository;

//(1) Importamos o "Documento Oficial" (Entidade)
//que este "Arquivista" vai gerenciar.
import com.batedeira.projeto.entity.Batelada;

//(2) Importações do "Crachá" do Spring Data
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
* REVISÃO (ARQUIVO 13 de N)
*
* Classe: BateladaRepository.java (Interface)
* Papel: Este é o "Arquivista" (o Repository) dos "Relatórios".
*
* Objetivo: É a interface que o "Robozão" (Service) vai usar
* para dar ordens ao banco de dados (ex: "Salve este Relatório",
* "Busque todos os Relatórios de ontem").
*/
@Repository // (3) O "Crachá" de Arquivista.
public interface BateladaRepository extends JpaRepository<Batelada, Long> {
 /**
  * (4) A "Mágica" do Spring Data JPA: extends JpaRepository
  *
  * Mesma mágica do ReceitaRepository. Ao "estender" JpaRepository,
  * estamos dizendo:
  *
  * "Sr. Spring, por favor, me dê automaticamente todos os
  * métodos básicos para gerenciar o 'Documento Oficial'
  * chamado 'Batelada' (o 1º parâmetro), cuja Chave Primária (PK)
  * é do tipo 'Long' (o 2º parâmetro)."
  *
  * GRAÇAS A ESSA LINHA, esta interface AGORA JÁ SABE FAZER:
  *
  * - .save(Batelada doc) -> (Salva um 'Relatório' Batelada)
  * - .findById(Long id) -> (Busca um 'Relatório' pelo ID)
  * - .findAll() -> (Busca TODOS os 'Relatórios')
  * - ... e muitos outros!
  *
  * O "Robozão" (Service) vai usar o '.save()' daqui
  * para salvar o "Relatório Mãe" (Batelada) e o
  * "Relatório Filho" (EtapaExecutada) no banco.
  */
  
  // (Futuramente, quando formos fazer os Relatórios (RF07),
  // vamos adicionar métodos personalizados aqui, como:)
  //
  // List<Batelada> findByDataInicioBetween(LocalDateTime inicio, LocalDateTime fim);
  //
  // (Mas, por agora, os métodos básicos são suficientes.)
}