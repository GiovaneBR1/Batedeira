package com.batedeira.api.domain.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // A mágica do Spring Data: só de escrever o nome, ele cria o SQL.
    UserDetails findByLogin(String login);
    
}