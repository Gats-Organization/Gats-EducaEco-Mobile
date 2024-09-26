package com.mobile.educaeco.models_api;

public class Aluno {
    private Long id;

    private String nome;

    private String sobrenome;

    private String email;

    private String senha;

    private Integer xp;

    private Turma turma;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public Integer getXp() {
        return xp;
    }

    public Turma getTurma() {
        return turma;
    }
}

