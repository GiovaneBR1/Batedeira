package com.batedeira.projeto.entity;

//Importações do JPA
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

/**
* (1) @Entity: Marca como "Documento Oficial" (tabela).
* Esta entidade armazena os dados de login do Administrador.
*/
@Entity
@Table(name = "usuario") // Mapeia para a tabela do MER
public class Usuario {

 /**
  * (2) @Id e @GeneratedValue: Chave Primária (PK) com Auto-Incremento.
  */
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 @Column(name = "id", nullable = false)
 private Long id;

 /**
  * (3) O login do usuário (ex: "admin").
  * 'unique = true' garante que não existam dois usuários
  * com o mesmo login, como definimos no MER.
  */
 @Column(name = "login", nullable = false, unique = true)
 private String login;

 /**
  * (4) O hash da senha.
  * Esta coluna armazena o resultado do BCrypt (RNF06).
  * NUNCA a senha pura!
  */
 @Column(name = "senha_hash", nullable = false)
 private String senhaHash;


 // --- Getters e Setters ---
 // (O JPA precisa deles)

 public Long getId() {
     return id;
 }

 public void setId(Long id) {
     this.id = id;
 }

 public String getLogin() {
     return login;
 }

 public void setLogin(String login) {
     this.login = login;
 }

 public String getSenhaHash() {
     return senhaHash;
 }

 public void setSenhaHash(String senhaHash) {
     this.senhaHash = senhaHash;
 }
}
