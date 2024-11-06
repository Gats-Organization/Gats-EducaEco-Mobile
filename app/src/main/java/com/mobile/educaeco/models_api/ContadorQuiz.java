package com.mobile.educaeco.models_api;

public class ContadorQuiz {
    private String email_aluno;
    private Integer quizzes_feitos;

    // Construtor vazio
    public ContadorQuiz() {}

    // Getters e Setters
    public String getEmail_aluno() {
        return email_aluno;
    }

    public void setEmail_aluno(String email_aluno) {
        this.email_aluno = email_aluno;
    }

    public Integer getQuizzes_feitos() {
        return quizzes_feitos;
    }

    public void setQuizzes_feitos(Integer quizzes_feitos) {
        this.quizzes_feitos = quizzes_feitos;
    }
}
