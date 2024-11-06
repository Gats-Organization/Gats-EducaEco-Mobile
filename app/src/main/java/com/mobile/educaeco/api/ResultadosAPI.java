package com.mobile.educaeco.api;

import com.mobile.educaeco.models_api.Resultado;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ResultadosAPI {
    @GET("/api/resultados/listar")
    Call<List<Resultado>> listarResultados(
            @Query("filtroResultado") String filtroResultado,
            @Query("filtroData") String filtroData
    );

}
