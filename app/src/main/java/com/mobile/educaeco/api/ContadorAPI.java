package com.mobile.educaeco.api;

import com.mobile.educaeco.models_api.Aluno;
import com.mobile.educaeco.models_api.ContadorJogo;
import com.mobile.educaeco.models_api.ContadorQuiz;
import com.mobile.educaeco.models_api.ContadorVideo;

import java.util.List;

import retrofit2.Call;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ContadorAPI {

    @POST("/incrementar-contador-videos-vistos/{email_aluno}")
    Call<Void> incrementarContadorVideosVistos(@Path("email_aluno") String emailAluno);

    @GET("/verificar-contador-videos-vistos/{email_aluno}")
    Call<ContadorVideo> verificarContadorVideosVistos(@Path("email_aluno") String emailAluno);

    @POST("/incrementar-contador-vezes-jogadas/{email_aluno}")
    Call<Void> incrementarContadorVezesJogadas(@Path("email_aluno") String emailAluno);

    @GET("/verificar-contador-vezes-jogadas/{email_aluno}")
    Call<ContadorJogo> verificarContadorVezesJogadas(@Path("email_aluno") String emailAluno);
    @POST("/incrementar-contador-quizzes-feitos/{email_aluno}")
    Call<Void> incrementarContadorQuizzesFeitos(@Path("email_aluno") String emailAluno);

    @GET("/verificar-contador-quizzes-feitos/{email_aluno}")
    Call<ContadorQuiz> verificarContadorQuizzesFeitos(@Path("email_aluno") String emailAluno);
}
