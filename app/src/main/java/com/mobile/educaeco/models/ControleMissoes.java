package com.mobile.educaeco.models;

public class ControleMissoes {
    boolean status;
    long id_aluno;
    long id_missao;

    public ControleMissoes( boolean status, long id_aluno, long id_missao) {
        this.status = status;
        this.id_aluno = id_aluno;
        this.id_missao = id_missao;
    }


    public boolean isStatus() {
        return status;
    }


    public long getId_aluno() {
        return id_aluno;
    }


    public long getId_missao() {
        return id_missao;
    }


    public void setStatus(boolean status) {
        this.status = status;
    }


    public void setId_aluno(long id_aluno) {
        this.id_aluno = id_aluno;
    }


    public void setId_missao(long id_missao) {
        this.id_missao = id_missao;
    }

    @Override
    public String toString() {
        return "ControleMissoes{" +
                ", status=" + status +
                ", id_aluno=" + id_aluno +
                ", id_missao=" + id_missao +
                '}';
    }
}
