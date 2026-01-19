package com.batedeira.projeto.repository;

//(1) Importamos o "Documento Oficial" (Entidade)
//que este "Arquivista" vai gerenciar.
import com.batedeira.projeto.entity.Receita;

//(2) Importações do "Crachá" do Spring Data
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
* REVISÃO (ARQUIVO 12 de N)
*
* Classe: ReceitaRepository.java (Interface)
* Papel: Este é o "Arquivista" (o Repository).
*
* Objetivo: É a interface que o  (Service) vai usar
* para dar ordens ao banco de dados (ex: "Salve esta Ficha",
* "Busque a Ficha ID 22").
*/
@Repository // (3) O "Crachá" de Arquivista.
         // Diz ao "Sr. Spring": "Eu sou um Repositório.
         // Minha função é falar com o banco."
public interface ReceitaRepository extends JpaRepository<Receita, Long> {
 /**
  * (4) A "Mágica" do Spring Data JPA: extends JpaRepository
  *
  * Ao "estender" (herdar) JpaRepository, nós dizemos:
  *
  * "Sr. Spring, por favor, me dê automaticamente todos os
  * métodos básicos para gerenciar a tabela  'Receita'
  * (o 1º parâmetro), cuja Chave Primária (PK)
  * é do tipo 'Long' (o 2º parâmetro)."
  *
  * GRAÇAS A ESSA LINHA, esta interface AGORA JÁ SABE FAZER:
  *
  * - .save(Receita doc) -> (Salva um 'Documento' Receita)
  * - .findById(Long id) -> (Busca um 'Documento' Receita pelo ID)
  * - .findAll() -> (Busca TODOS os 'Documentos' Receita)
  * - .deleteById(Long id) -> (Deleta um 'Documento' Receita)
  * - ... e muitos outros!
  *
  * É por isso que o "Robozão" (Service) vai poder chamar
  * 'receitaRepository.findById(22)' (na Cena 4, Passo 3).
  */
  
  // (Não precisamos escrever NADA aqui, pois os métodos
  // básicos do JpaRepository são tudo o que precisamos por agora.)
}
