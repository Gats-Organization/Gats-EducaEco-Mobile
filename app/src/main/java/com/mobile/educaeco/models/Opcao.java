package com.mobile.educaeco.models;

import androidx.annotation.NonNull;

public class Opcao {
    private long id;
    private String opcao;

    public Opcao() {
    }

    public Opcao(String opcao) {
        this.opcao = opcao;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOpcao() {
        return opcao;
    }

    public void setOpcao(String opcao) {
        this.opcao = opcao;
    }

    @NonNull
    @Override
    public String toString() {
        return "Opcao{" +
                "id=" + id +
                ", opcao='" + opcao + '\'' +
                '}';
    }
}
