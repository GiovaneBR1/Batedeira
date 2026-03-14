package com.batedeira.projeto.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
//(2) Importações do "Crachá" do Spring Data
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.batedeira.projeto.entity.Batelada;
import com.batedeira.projeto.entity.enums.modoBatedeira;
import com.batedeira.projeto.entity.enums.statusBatelada;


@Repository 
public interface BateladaRepository extends JpaRepository<Batelada, Long> {


	//traz a ultima batelada registrada no banco
	Optional<Batelada> findTopByOrderByDataFimDesc();
	
	/* SELECT b quer dizer: me devolva o objeto b INTEIRO
	 * FROM Batelada b quer dizer: na tabela batelada olhe os registros um a um;
	 * para cada registro, atribui-se uma variavel temporaria de b.
	 */
	@Query("SELECT b FROM Batelada b WHERE " +
		       "(:modo IS NULL OR b.modo = :modo) AND " + //"O modo do objeto 'b' tem de ser igual à variável modo que eu passei"
		       "(:status IS NULL OR b.status = :status) AND " +
		       "(:dataInicio IS NULL OR b.dataInicio >= :dataInicio) AND " +
		       "(:dataFim IS NULL OR b.dataInicio <= :dataFim)")
		Page<Batelada> buscarComFiltros(
		        @Param("modo") modoBatedeira modo,
		        @Param("status") statusBatelada status,
		        @Param("dataInicio") LocalDateTime dataInicio,
		        @Param("dataFim") LocalDateTime dataFim,
		        Pageable pageable);
	
}