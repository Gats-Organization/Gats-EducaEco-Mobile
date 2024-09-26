package com.mobile.educaeco.models_api;


public class Escola {
    private Long id;

    private String nome;

    private String email;

    private String telefone;

    private Endereco endereco;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public Endereco getEndereco() {
        return endereco;
    }
}

