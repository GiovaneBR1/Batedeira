package com.batedeira.projeto.config;

//Pacote: com.seuprojeto.batedeira.config


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
* REVISÃO (ARQUIVO EXTRA)
*
* Classe: SecurityConfig.java
* Papel: Este é o "Manual de Instruções" do "Guarda-Costas"
* (Spring Security).
*
* Objetivo: Dizer ao "Guarda-Costas" quais "portas" (endpoints)
* ele deve liberar para o nosso teste com o Postman.
*/
@Configuration // (1) Crachá: "Eu sou uma classe de Configuração."
@EnableWebSecurity // (2) Crachá: "Eu vou reescrever as regras do 'Guarda-Costas'."
public class SecurityConfig {

 /**
  * (3) O "Manual de Regras" (O Bean)
  * Este método define a nova "Lista de Regras" do Guarda-Costas.
  */
 @Bean
 public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
     
     // (4) As Instruções (o "Builder"):
     http
         /**
          * (5) REGRA 1: Desligar a proteção "CSRF".
          * Esta regra é para "sites" (navegadores).
          * Ela QUEBRA o Postman e o ESP32. Temos que desligar.
          */
         .csrf(AbstractHttpConfigurer::disable)
         
         /**
          * (6) REGRA 2: A "Lista de Liberação" (Autorização).
          * Aqui dizemos quais portas NÃO PRECISAM de login.
          */
         .authorizeHttpRequests(auth -> auth
             
             // (7) "Guarda-Costas, libere a 'Caixa de Correio' do ESP32"
             .requestMatchers("/api/v1/bateladas/").permitAll()
             
             // (8) "Guarda-Costas, libere a 'Sala de Documentação' (Swagger)"
             .requestMatchers("/swagger-ui.html").permitAll()
             .requestMatchers("/swagger-ui/**").permitAll()
             .requestMatchers("/api-docs/**").permitAll()
             .requestMatchers("/v3/api-docs/**").permitAll()
             
             // (9) "Guarda-Costas, para TODAS as OUTRAS portas, exija login."
             // (Isso vai proteger o futuro 'Painel do Admin')
             .anyRequest().authenticated()
         );

     // (10) Retorna o "Manual" treinado.
     return http.build();
 }
}