package com.mobile.educaeco.models;

public class Aluno {
    private String nome;
    private String email;
    private int xp;


    public Aluno() {
    }


    public Aluno(String nome, String email, int xp) {
        this.nome = nome;
        this.email = email;
        this.xp = xp;
    }

    public String getNome() {
        return nome;
    }


    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public int getXp() {
        return xp;
    }


    public void setXp(int xp) {
        this.xp = xp;
    }


    @Override
    public String toString() {
        return "Aluno{" +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", xp=" + xp +
                '}';
    }
}
