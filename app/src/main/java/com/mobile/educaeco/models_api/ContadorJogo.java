package com.mobile.educaeco.models_api;

public class ContadorJogo{
    private String email_aluno;
    private Integer vezes_jogadas;

    // Construtor vazio
    public ContadorJogo() {}

    // Getters e Setters
    public String getEmail_aluno() {
        return email_aluno;
    }

    public void setEmail_aluno(String email_aluno) {
        this.email_aluno = email_aluno;
    }

    public Integer getVezes_jogadas() {
        return vezes_jogadas;
    }

    public void setVezes_jogadas(Integer vezes_jogadas) {
        this.vezes_jogadas = vezes_jogadas;
    }
}
