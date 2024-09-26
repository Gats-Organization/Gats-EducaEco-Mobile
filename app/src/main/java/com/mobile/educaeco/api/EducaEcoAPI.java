package com.mobile.educaeco.api;

import com.mobile.educaeco.models_api.Aluno;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface EducaEcoAPI {
    @GET("/api/alunos/{id}")
    Call<Aluno> getAluno(@Path("id") String id);

    @GET("/api/alunos/email/{email}")
    Call<Aluno> getAlunoByEmail(@Path("email") String email);


}
