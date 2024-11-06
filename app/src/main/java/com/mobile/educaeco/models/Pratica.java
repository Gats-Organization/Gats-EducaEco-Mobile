package com.mobile.educaeco.models;

import java.util.Date;

public class Pratica {

    private long id;
    private String pratica;
    private String status;
    private Date dataCriacao;
    private Date dataFinalizacao;
    private Date dataEntregaPratica;
    private String imagemPratica;
    private boolean validacao;



    public Pratica() {
    }

    public Pratica(long id, String pratica, String status, Date dataCriacao, Date dataFinalizacao, Date dataEntregaPratica, String imagemPratica, boolean validacao) {
        this.id = id;
        this.pratica = pratica;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.dataFinalizacao = dataFinalizacao;
        this.dataEntregaPratica = dataEntregaPratica;
        this.imagemPratica = imagemPratica;
        this.validacao = validacao;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPratica() {
        return pratica;
    }

    public void setPratica(String pratica) {
        this.pratica = pratica;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Date getDataEntregaPratica() {
        return dataEntregaPratica;
    }

    public void setDataEntregaPratica(Date dataEntregaPratica) {
        this.dataEntregaPratica = dataEntregaPratica;
    }

    public String getImagemPratica() {
        return imagemPratica;
    }

    public void setImagemPratica(String imagemPratica) {
        this.imagemPratica = imagemPratica;
    }

    public boolean isValidacao() {
        return validacao;
    }

    public void setValidacao(boolean validacao) {
        this.validacao = validacao;
    }

    @Override
    public String toString() {
        return "Pratica{" +
                "id=" + id +
                ", pratica='" + pratica + '\'' +
                ", status='" + status + '\'' +
                ", dataCriacao=" + dataCriacao +
                ", dataFinalizacao=" + dataFinalizacao +
                '}';
    }
}
