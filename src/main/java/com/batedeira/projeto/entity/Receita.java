package com.batedeira.projeto.entity;

//imports do JPA 
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity //representa uma tabela no banco
@Table(name = "receita") //mapeia o nome da tabela
public class Receita {

	@Id //indica que é PK
	
	//Um @Column mapeia nomes das colunas na tabela
	
	@Column(name="id",nullable = false)
	private long id;
	
	//nome que o admin dá a receita
	//nullable = false indica que a ccoluna não pode ficar vazia
	@Column(name="nome_amigavel" , nullable = false)
	private String nomeAmigavel;
	
	//data/hora que o service viu esta receita pela primeira vez
	@Column(name="data_descoberta" , nullable = false)
	private LocalDateTime dataDescoberta;
	
	
	//O switch que o admin usa para desativar uma receita
	@Column(name="ativa", nullable = false)
	private boolean ativa = true;
	
	@OneToMany(mappedBy = "receita", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EtapaReceita> etapas = new ArrayList<>(); // <-- Tem que ter esse = new ArrayList<>();
    
	public void addEtapa(EtapaReceita etapa) {
		this.etapas.add(etapa);
		
		etapa.setReceita(this);
		
		
	}
	
	public List<EtapaReceita> getEtapas() {
        return etapas;
    }
	
	public void setEtapas(List<EtapaReceita> etapas) {
        this.etapas = etapas;
    }

    // --- Getters e Setters ---
    // O JPA (o "Arquivista") precisa deles para
    // "ler" e "escrever" nos campos privados deste documento.

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNomeAmigavel() {
		return nomeAmigavel;
	}

	public void setNomeAmigavel(String nomeAmigavel) {
		this.nomeAmigavel = nomeAmigavel;
	}

	public LocalDateTime getDataDescoberta() {
		return dataDescoberta;
	}

	public void setDataDescoberta(LocalDateTime dataDescoberta) {
		this.dataDescoberta = dataDescoberta;
	}

	public boolean isAtiva() {
		return ativa;
	}

	public void setAtiva(boolean ativa) {
		this.ativa = ativa;
	} 
	
}
