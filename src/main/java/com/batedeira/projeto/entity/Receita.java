package com.batedeira.projeto.entity;

//imports do JPA 
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import java.util.ArrayList;
import java.util.List;

@Entity // representa uma tabela no banco
@Table(name = "receita") // mapeia o nome da tabela
public class Receita {

	@Id //PK
	@Column(name = "id", nullable = false)
	private long id;

	@OneToMany(mappedBy = "receita", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EtapaReceita> etapas = new ArrayList<>();

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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
