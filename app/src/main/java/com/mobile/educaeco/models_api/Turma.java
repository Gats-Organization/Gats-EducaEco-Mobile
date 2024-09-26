package com.mobile.educaeco.models_api;

public class Turma {
    private Long id;

    private String nomenclatura;

    private String serie;

    private int ano;

    private Escola escola;

    private Professor professor;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getNomenclatura() {
        return nomenclatura;
    }

    public String getSerie() {
        return serie;
    }

    public int getAno() {
        return ano;
    }

    public Escola getEscola() {
        return escola;
    }

    public Professor getProfessor() {
        return professor;
    }
}

