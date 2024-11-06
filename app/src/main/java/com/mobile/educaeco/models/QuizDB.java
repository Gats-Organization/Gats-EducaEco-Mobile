package com.mobile.educaeco.models;

import java.util.Date;
import java.util.List;

public class QuizDB {
    private String tema;
    private List<String> perguntasIds;

    public QuizDB(String tema, List<String> perguntasIds) {
        this.tema = tema;
        this.perguntasIds = perguntasIds;
    }

    public String getTema() {
        return tema;
    }

    public List<String> getPerguntasIds() {
        return perguntasIds;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public void setPerguntasIds(List<String> perguntasIds) {
        this.perguntasIds = perguntasIds;
    }

    @Override
    public String toString() {
        return tema;
    }
}

