package com.mobile.educaeco.models;

public class Missao {

    private long id;
    private String descricao;
    private int quantXp;
    private boolean status;
    private int id_aluno;

    public Missao() {
    }

    public Missao (String descricao, int quantXp) {
        this.descricao = descricao;
        this.quantXp = quantXp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getQuantXp() {
        return quantXp;
    }

    public void setQuantXp(int quantXp) {
        this.quantXp = quantXp;
    }

    @Override
    public String toString() {
        return "Missao{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                ", quantXp=" + quantXp +
                '}';
    }
}
