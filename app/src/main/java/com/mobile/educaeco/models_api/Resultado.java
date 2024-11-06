package com.mobile.educaeco.models_api;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
public class Resultado {

    private String nome;

    private String email;

    private LocalDateTime dataRegistro;

    private String resultado;

    // Construtores, Getters e Setters
    public Resultado() {}

    public Resultado(String nome, String email, LocalDateTime dataRegistro, String resultado) {
        this.nome = nome;
        this.email = email;
        this.dataRegistro = dataRegistro;
        this.resultado = resultado;
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }
    public String getResultado() { return resultado; }

    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
    public void setDataRegistro(LocalDateTime dataRegistro) { this.dataRegistro = dataRegistro; }
    public void setResultado(String resultado) { this.resultado = resultado; }
}
