package com.batedeira.projeto.repository;

import com.batedeira.projeto.entity.ParametrosGlobal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParamRepository extends JpaRepository<ParametrosGlobal, Long> {
    
}