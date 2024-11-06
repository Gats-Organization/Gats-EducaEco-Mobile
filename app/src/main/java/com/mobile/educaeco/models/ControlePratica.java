package com.mobile.educaeco.models;

import android.widget.ImageView;

import java.util.Date;

public class ControlePratica {
    private String status;
    private Date dataEntrega;
    private String imgPratica;


    // Construtor padrão (obrigatório para Firebase)
    public ControlePratica() {}


    // Construtor com parâmetros
    public ControlePratica(String status, Date dataEntrega, String imgPratica) {
        this.status = status;
        this.dataEntrega = dataEntrega;
        this.imgPratica = imgPratica;
    }


    // Getters e Setters

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(Date dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public String getImgPratica() {
        return imgPratica;
    }


    public void setImgPratica(String imgPratica) {
        this.imgPratica = imgPratica;
    }

    @Override
    public String toString() {
        return "ControlePratica{" +
                "status='" + status + '\'' +
                ", dataEntrega=" + dataEntrega +
                ", imgPratica=" + imgPratica +
                '}';
    }
}
