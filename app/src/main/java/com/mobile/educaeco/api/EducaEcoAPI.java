package com.mobile.educaeco.api;

import com.mobile.educaeco.models_api.Admin;
import com.mobile.educaeco.models_api.Aluno;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EducaEcoAPI {
    @GET("/api/alunos/{id}")
    Call<Aluno> getAluno(@Path("id") String id);

    @GET("/api/alunos/email/{email}")
    Call<Aluno> getAlunoByEmail(@Path("email") String email);

    @PUT("/api/alunos/atualizar-senha")
    Call<Void> atualizarSenha(
            @Query("email") String email,
            @Query("novaSenha") String novaSenha
    );

    @PUT("/api/alunos/atualizar-xp")
    Call<Void> atualizarXp(
            @Query("email") String email,
            @Query("somaXp") int somaXp
    );

    @GET("/api/alunos/turma/{id_turma}")
    Call<List<Aluno>> getAlunosByTurma(@Path("id_turma") String id_turma);

    @GET("api/admin/selecionar")
    Call<List<Admin>> getAdmins();
}
