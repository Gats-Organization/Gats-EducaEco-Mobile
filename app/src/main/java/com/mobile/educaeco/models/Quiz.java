package com.mobile.educaeco.models;

import java.util.Date;
import java.util.List;

public class Quiz {
    private String id;
    private String tema;
    private Date dataCriacao;
    private Date dataFinalizacao;
    private List<String> perguntasIds;

    public Quiz() {
    }

    public Quiz(String id, String tema, Date dataCriacao, Date dataFinalizacao, List<String> perguntasIds) {
        this.id = id;
        this.tema = tema;
        this.dataCriacao = dataCriacao;
        this.dataFinalizacao = dataFinalizacao;
        this.perguntasIds = perguntasIds;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Date getDataFinalizacao() {
        return dataFinalizacao;
    }

    public void setDataFinalizacao(Date dataFinalizacao) {
        this.dataFinalizacao = dataFinalizacao;
    }

    public List<String> getPerguntasIds() {
        return perguntasIds;
    }

    public void setPerguntasIds(List<String> perguntasIds) {
        this.perguntasIds = perguntasIds;
    }

    @Override
    public String toString() {
        return tema;
    }
}

