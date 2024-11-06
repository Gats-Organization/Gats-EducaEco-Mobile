package com.mobile.educaeco.models_api;

public class ContadorVideo {
    private String email_aluno;
    private Integer videos_vistos;

    // Construtor vazio
    public ContadorVideo() {}

    // Getters e Setters
    public String getEmail_aluno() {
        return email_aluno;
    }

    public void setEmail_aluno(String email_aluno) {
        this.email_aluno = email_aluno;
    }

    public Integer getVideos_vistos() {
        return videos_vistos;
    }

    public void setVideos_vistos(Integer videos_vistos) {
        this.videos_vistos = videos_vistos;
    }
}
