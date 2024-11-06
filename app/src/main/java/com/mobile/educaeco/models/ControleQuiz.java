package com.mobile.educaeco.models;

public class ControleQuiz {
    private double nota;
    private int quantAcertos;
    private int quantPerguntas;
    private String duracao;

    // Construtor padrão (obrigatório para Firebase)
    public ControleQuiz() {}

    // Construtor com parâmetros
    public ControleQuiz(double nota, int quantAcertos, int quantoPerguntas, String duracao) {
        this.nota = nota;
        this.quantAcertos = quantAcertos;
        this.quantPerguntas = quantoPerguntas;
        this.duracao = duracao;
    }

    // Getters e Setters

    public double getNota() {
        return nota;
    }

    public int getQuantAcertos() {
        return quantAcertos;
    }

    public int getQuantPerguntas() {
        return quantPerguntas;
    }

    public String getDuracao() {
        return duracao;
    }
}
