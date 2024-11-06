package com.mobile.educaeco.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mobile.educaeco.R;
import com.mobile.educaeco.adapters.AdapterLocalDateTime;
import com.mobile.educaeco.adapters.AdapterResultados;
import com.mobile.educaeco.api.ResultadosAPI;
import com.mobile.educaeco.models_api.Resultado;

import java.time.LocalDateTime;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminResultados extends AppCompatActivity {

    private AdapterResultados adapterResultados;
    private ResultadosAPI api;
    private String filtroResultado = null;
    private String filtroData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_resultados);

        ImageView btnVoltar = findViewById(R.id.voltar);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminResultados.this, Admin.class);
                startActivity(intent);
            }
        });

        // Inicializa Retrofit
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new AdapterLocalDateTime())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gats-educaeco-api-mongodb.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        api = retrofit.create(ResultadosAPI.class);

        api = retrofit.create(ResultadosAPI.class);

        // Configura RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewResultados);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterResultados = new AdapterResultados();
        recyclerView.setAdapter(adapterResultados);

        Button btnEstudantil = findViewById(R.id.btnEstudantil);
        Button btnSemMotivacao = findViewById(R.id.btnSemMotivacao);
        Button btnHoje = findViewById(R.id.btnHoje);
        Button btnEssaSemana = findViewById(R.id.btnEssaSemana);
        Button btnEsseMes = findViewById(R.id.btnEsseMes);
        boolean[] selecionado = new boolean[5];

        ResultadosAPI api = retrofit.create(ResultadosAPI.class);
        api.listarResultados(filtroResultado, filtroData);

        fetchResultados();

        btnEstudantil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionado[0] = !selecionado[0];
                selecionado[1] = false;
                if (selecionado[0]) {
                    btnEstudantil.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#277E93")));
                    selecionado[1] = false;
                    btnSemMotivacao.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4AAFC6")));
                    filtroResultado = "Estudantil";
                    Log.d("FiltroResultado", filtroResultado);
                    fetchResultados();
                } else {
                    btnEstudantil.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4AAFC6")));
                    filtroResultado = null;
                    fetchResultados();
                }

            }
        });

        btnSemMotivacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionado[1] = !selecionado[1];
                selecionado[0] = false;
                if (selecionado[1]) {
                    btnSemMotivacao.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#277E93")));
                    selecionado[0] = false;
                    btnEstudantil.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4AAFC6")));
                    filtroResultado = "Não tenho motivação";
                    Log.d("FiltroResultado", filtroResultado);
                    fetchResultados();
                } else {
                    btnSemMotivacao.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4AAFC6")));
                    filtroResultado = null;
                    fetchResultados();
                }
            }
        });


        btnHoje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionado[2] = !selecionado[2];
                selecionado[3] = false;
                selecionado[4] = false;

                if (selecionado[2]) {
                    btnHoje.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#277E93")));
                    selecionado[3] = false;
                    selecionado[4] = false;
                    btnEssaSemana.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4AAFC6")));
                    btnEsseMes.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4AAFC6")));
                    filtroData = "Hoje";
                    fetchResultados();
                } else {
                    btnHoje.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4AAFC6")));
                    filtroData = null;
                    fetchResultados();
                }
            }
        });

        btnEssaSemana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionado[3] = !selecionado[3];
                selecionado[2] = false;
                selecionado[4] = false;
                if (selecionado[3]) {
                    btnEssaSemana.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#277E93")));
                    selecionado[2] = false;
                    selecionado[4] = false;
                    btnHoje.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4AAFC6")));
                    btnEsseMes.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4AAFC6")));
                    filtroData = "Essa semana";
                    fetchResultados();
                } else {
                    btnEssaSemana.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4AAFC6")));
                    filtroData = null;
                    fetchResultados();
                }
            }
        });

        btnEsseMes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionado[4] = !selecionado[4];
                selecionado[2] = false;
                selecionado[3] = false;
                if (selecionado[4]) {
                    btnEsseMes.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#277E93")));
                    selecionado[2] = false;
                    selecionado[3] = false;
                    btnHoje.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4AAFC6")));
                    btnEssaSemana.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4AAFC6")));
                    filtroData = "Esse mês";
                    fetchResultados();
                } else {
                    btnEsseMes.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4AAFC6")));
                    filtroData = null;
                    fetchResultados();
                }
            }
        });


    }

    private void fetchResultados() {
        // Chama a API para obter os resultados filtrados
        Call<List<Resultado>> call = api.listarResultados(filtroResultado, filtroData);
        call.enqueue(new Callback<List<Resultado>>() {
            @Override
            public void onResponse(@NonNull Call<List<Resultado>> call, @NonNull Response<List<Resultado>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Atualiza o adapter com os novos dados
                    adapterResultados.setResultados(response.body());
                } else {
                    Log.e("API Error", "Erro ao carregar resultados.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Resultado>> call, @NonNull Throwable t) {
                Log.e("API Failure", "Falha na requisição: " + t.getMessage());
            }
        });
    }
}