package com.mobile.educaeco.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.educaeco.Database;
import com.mobile.educaeco.R;
import com.mobile.educaeco.api.ContadorAPI;
import com.mobile.educaeco.api.EducaEcoAPI;
import com.mobile.educaeco.interfaces.XPCallback;
import com.mobile.educaeco.models_api.ContadorQuiz;
import com.mobile.educaeco.models_api.ContadorVideo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResultadoQuiz extends AppCompatActivity {

    ContadorAPI api;
    EducaEcoAPI apiEducaEco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_quiz);


    ImageView btnVoltarQuizzes = findViewById(R.id.btnVoltarQuiz);

    btnVoltarQuizzes.setOnClickListener(v -> {
        Bundle bundle = new Bundle();
        bundle.putString("tela", "quiz");
        Intent intent = new Intent(ResultadoQuiz.this, Main.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    });

    TextView nota = findViewById(R.id.nota);
    TextView certas = findViewById(R.id.questoesCertas);
    TextView tempo = findViewById(R.id.tempoDuracao);
    TextView tema = findViewById(R.id.temaQuiz);

    Bundle bundle = getIntent().getExtras();
    if(bundle != null) {
        double notaDouble = bundle.getDouble("nota");
        int certasInt = bundle.getInt("totalAcertos");
        int totalQuestoes = bundle.getInt("totalQuestoes");
        String tempoDuracao = bundle.getString("duracao");
        String temaQuiz = bundle.getString("temaQuiz");
        String idQuiz = bundle.getString("idQuiz");
        boolean incrementar = bundle.getBoolean("incrementar");

        nota.setText(String.format("%.2f", notaDouble).replace(".", ","));
        certas.setText(String.valueOf(certasInt) + "/" + String.valueOf(totalQuestoes));
        tempo.setText(tempoDuracao);
        tema.setText(temaQuiz);

        if ( incrementar ) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://gats-educaeco-api-neo4j-an34.onrender.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            api = retrofit.create(ContadorAPI.class);

            Retrofit retrofit2 = new Retrofit.Builder()
                    .baseUrl("https://gats-educaeco-api-dev2-pe6e.onrender.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiEducaEco = retrofit2.create(EducaEcoAPI.class);

            SharedPreferences sharedPreferences = getSharedPreferences("aluno", MODE_PRIVATE);
            String email_aluno = sharedPreferences.getString("email", "");

            Call<Void> call = api.incrementarContadorQuizzesFeitos(email_aluno);

            call.enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                    Call<ContadorQuiz> call2 = api.verificarContadorQuizzesFeitos(email_aluno);

                    call2.enqueue(new retrofit2.Callback<ContadorQuiz>() {
                        @Override
                        public void onResponse(Call<ContadorQuiz> call, retrofit2.Response<ContadorQuiz> response) {
                            ContadorQuiz contadorQuiz = response.body();
                            Integer contador = contadorQuiz.getQuizzes_feitos();
                            if ( contador == 1 ) {
                                Database db = new Database();
                                db.getXPbyMissao(3, new XPCallback() {
                                    @Override
                                    public void onCallback(int xp) {
                                        String email_aluno = sharedPreferences.getString("email", "");

                                        Call<Void> call3 = apiEducaEco.atualizarXp(email_aluno, xp);
                                        db.updateXp(email_aluno, xp);
                                        call3.enqueue(new Callback<Void>() {
                                            @Override
                                            public void onResponse(Call<Void> call, Response<Void> response) {
                                                if ( response.isSuccessful() ) {
                                                    //Manda a notificação e atualiza o status da missão
                                                    String id_aluno = sharedPreferences.getString("id_aluno", "");
                                                    db.updateStatus(Long.parseLong(String.valueOf(id_aluno)), 3L);
                                                    int xpNovo = sharedPreferences.getInt("xp", 0) + xp;
                                                    SharedPreferences.Editor editor = getSharedPreferences("aluno", MODE_PRIVATE).edit();
                                                    editor.putInt("xp", xpNovo);
                                                    missaoQuizFeito();

                                                    Log.e("Sucesso", "FUNCIONOUUU" + response.message());
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Void> call, Throwable t) {

                                                Log.e("Erro", "ERRO" + t.getMessage());
                                            }
                                        });
                                    }
                                });
                            } else {
                                Log.e("Missão", "Missão já foi concluída!" + response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<ContadorQuiz> call, Throwable t) {
                            Log.e("Erro", t.getMessage());
                        }
                    });
                    Log.e("Sucesso", response.message());
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("Erro", t.getMessage());
                }
            });
        }
    }
}

    public void missaoQuizFeito() {
        String channelID = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelID);
        builder.setSmallIcon(R.drawable.logo_educaeco)
                .setContentTitle("EBAAAA! Você concluiu uma missão!")
                .setContentText("Receba 500xp por ter concluído a missão de Faça um quiz quando o seu professor atribuir")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelID);

            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(channelID, "CHANNEL_NAME", importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }

        }

        notificationManager.notify(1, builder.build());
    }
}