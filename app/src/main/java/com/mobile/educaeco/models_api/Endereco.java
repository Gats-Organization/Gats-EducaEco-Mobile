package com.mobile.educaeco.models_api;

public class Endereco {
    private Long id;
    private int numero;
    private String rua;
    private String bairro;

    private String cidade;
    private String estado;  // Corrigido para 30 caracteres, conforme a tabela

    private String cep;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getCep() {
        return cep;
    }

    public String getEstado() {
        return estado;
    }

    public String getCidade() {
        return cidade;
    }

    public String getBairro() {
        return bairro;
    }

    public int getNumero() {
        return numero;
    }
}

