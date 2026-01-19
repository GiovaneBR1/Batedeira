package com.batedeira.projeto.entity;

//Importações do JPA
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue; // (NOVO) Para auto-incremento
import jakarta.persistence.GenerationType; // (NOVO)
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne; // (NOVO) Para o relacionamento
import jakarta.persistence.JoinColumn; // (NOVO) Para a Chave Estrangeira
import jakarta.persistence.FetchType; // (NOVO) Otimização de Busca


/* --------------EtapaReceita-------------------*/
 /* Representa a tabela "etapa_receita" do banco 
  * Os EtapaReceita são os registros no banco que dizem ao Service exatamente o que deveria acontecer, 
  * qual a ordem, qual a quantidade e qual a tolerância permitida.
/*
* (1) @Entity: Marca como uma tabela.
*/
@Entity
@Table(name = "etapa_receita") // Mapeia para a tabela do MER
public class EtapaReceita {

 /**
  * (2) @Id: Marca como Chave Primária (PK).
  */
 @Id
 /**
  * (3) @GeneratedValue: Esta é DIFERENTE da Receita.
  * Estamos dizendo ao JPA: "O banco de dados (MySQL)
  * é responsável por gerar este ID (AUTO_INCREMENT)."
  */
 @GeneratedValue(strategy = GenerationType.IDENTITY) 
 @Column(name = "id", nullable = false)
 private Long id;

 // Colunas de dados (como no MER)
 
 @Column(name = "ordem", nullable = false)
 private int ordem;

 @Column(name = "acao_ou_ingrediente", nullable = false)
 private String acaoOuIngrediente;

 @Column(name = "quantidade_esperada", nullable = false)
 private double quantidadeEsperada;

 @Column(name = "unidade", nullable = false)
 private String unidade;

 /**
  * (4) A Coluna de Tolerância.
  * 'nullable = true' é o padrão, mas deixamos explícito
  * que ela PODE ser nula (como definimos no MER).
  */
 @Column(name = "tolerancia_percentual", nullable = true) 
 private Double toleranciaPercentual;


 // --- A MÁGICA DO RELACIONAMENTO (A Conexão) ---

 /**
  * (5) @ManyToOne: Esta é a Chave Estrangeira (FK).
  * Dizemos ao JPA: "MUITAS (Many) EtapaReceita
  * pertencem A UMA (One) Receita."
  *
  * (FetchType.LAZY): É uma otimização. Diz ao JPA:
  * "Não carregue o objeto 'Receita' inteiro do banco,
  * a menos que eu peça explicitamente."
  */
 @ManyToOne(fetch = FetchType.LAZY)
 /**
  * (6) @JoinColumn: Especifica qual é a coluna no banco
  * que faz essa ligação (a FK).
  * O nome "receita_id" é o mesmo que definimos no nosso MER.
  * 'nullable = false' garante que nenhuma etapa exista
  * sem uma receita-mãe.
  */
 @JoinColumn(name = "receita_id", nullable = false)
 private Receita receita; // (7) O objeto Java da "mãe"


 // --- Getters e Setters ---
 // (O JPA precisa deles)

 public Long getId() {
     return id;
 }

 public void setId(Long id) {
     this.id = id;
 }

 public int getOrdem() {
     return ordem;
 }

 public void setOrdem(int ordem) {
     this.ordem = ordem;
 }

 public String getAcaoOuIngrediente() {
     return acaoOuIngrediente;
 }

 public void setAcaoOuIngrediente(String acaoOuIngrediente) {
     this.acaoOuIngrediente = acaoOuIngrediente;
 }

 public double getQuantidadeEsperada() {
     return quantidadeEsperada;
 }

 public void setQuantidadeEsperada(double quantidadeEsperada) {
     this.quantidadeEsperada = quantidadeEsperada;
 }

 public String getUnidade() {
     return unidade;
 }

 public void setUnidade(String unidade) {
     this.unidade = unidade;
 }

 public Double getToleranciaPercentual() {
     return toleranciaPercentual;
 }

 public void setToleranciaPercentual(Double toleranciaPercentual) {
     this.toleranciaPercentual = toleranciaPercentual;
 }

 public Receita getReceita() {
     return receita;
 }

 public void setReceita(Receita receita) {
     this.receita = receita;
 }
}