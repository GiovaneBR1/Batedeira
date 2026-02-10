package com.batedeira.api.domain.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//classe responsável pela conexão e "tradução" entre e o banco de dados e o Spring Security

@Service // carrega na memória assim que o programa iniciar

public class AutenticacaoService implements UserDetailsService { //

	@Autowired //injeção de dependência
	private UsuarioRepository repository;

	@Override /*o loadbyusername acesso o repository(banco),busca o registro e devolve pro spring security */								
		public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { 
		return repository.findByLogin(username);
	}
}