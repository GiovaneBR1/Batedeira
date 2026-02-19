package com.batedeira.projeto.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity // 
public class ParametrosGlobal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double toleranciaDef = 5.0;

    // Getters e Setters obrigatórios
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getToleranciaDef() { return toleranciaDef; }
    public void setToleranciaDef(Double toleranciaDef) { this.toleranciaDef = toleranciaDef; }
}