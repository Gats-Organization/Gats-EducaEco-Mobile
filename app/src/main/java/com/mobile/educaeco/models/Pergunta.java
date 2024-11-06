package com.mobile.educaeco.models;

import java.util.List;

public class Pergunta {
    private String pergunta;
    private List<String> opcoes;
    private String opcao_correta;

    // Construtor sem argumentos
    public Pergunta() {}

    // Construtor com parâmetros
    public Pergunta(String pergunta, List<String> opcoes, String opcao_correta) {
        this.pergunta = pergunta;
        this.opcoes = opcoes;
        this.opcao_correta = opcao_correta;
    }

    // Getter para a pergunta
    public String getPergunta() {
        return pergunta;
    }

    // Setter para a pergunta com validação
    public void setPergunta(String pergunta) {
        if (pergunta == null || pergunta.isEmpty()) {
            throw new IllegalArgumentException("A pergunta não pode ser nula ou vazia.");
        }
        this.pergunta = pergunta;
    }

    // Getter para as opções
    public List<String> getOpcoes() {
        return opcoes;
    }

    // Setter para as opções com validação
    public void setOpcoes(List<String> opcoes) {
        if (opcoes == null || opcoes.isEmpty()) {
            throw new IllegalArgumentException("A lista de opções não pode ser nula ou vazia.");
        }
        this.opcoes = opcoes;
    }

    // Getter para a opção correta
    public String getOpcao_correta() {
        return opcao_correta;
    }

    // Setter para a opção correta
    public void setOpcao_correta(String opcao_correta) {
        if (opcao_correta == null || opcao_correta.isEmpty()) {
            throw new IllegalArgumentException("A opção correta não pode ser nula ou vazia.");
        }
        this.opcao_correta = opcao_correta;
    }

    @Override
    public String toString() {
        return "Pergunta{" +
                "pergunta='" + pergunta + '\'' +
                ", opcoes=" + opcoes +
                ", opcao_correta='" + opcao_correta + '\'' +
                '}';
    }
}
