package com.mobile.educaeco.activities;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mobile.educaeco.Database;
import com.mobile.educaeco.R;
import com.mobile.educaeco.api.ContadorAPI;
import com.mobile.educaeco.api.EducaEcoAPI;
import com.mobile.educaeco.interfaces.XPCallback;
import com.mobile.educaeco.models_api.ContadorJogo;
import com.mobile.educaeco.models_api.ContadorVideo;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityVideo extends AppCompatActivity {
    ContadorAPI api;
    EducaEcoAPI apiEducaEco;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        YouTubePlayerView video = findViewById(R.id.video);
        ImageView btnExpand = findViewById(R.id.btnExpand);

        Bundle bundle = getIntent().getExtras();
        String video_url = bundle.getString("video_url");
        video.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(video_url, 0);
            }
        });

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if ( ContextCompat.checkSelfPermission(ActivityVideo.this,
                    android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        ActivityVideo.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                Call<Void> call = api.incrementarContadorVideosVistos(email_aluno);

                call.enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                        Call<ContadorVideo> call2 = api.verificarContadorVideosVistos(email_aluno);

                        call2.enqueue(new retrofit2.Callback<ContadorVideo>() {
                            @Override
                            public void onResponse(Call<ContadorVideo> call, retrofit2.Response<ContadorVideo> response) {
                                ContadorVideo contadorVideo = response.body();
                                Integer contador = contadorVideo.getVideos_vistos();
                                if (  contador == 1 ) {
                                    Database db = new Database();
                                    db.getXPbyMissao(1, new XPCallback() {
                                        @Override
                                        public void onCallback(int xp) {
                                            String email_aluno = sharedPreferences.getString("email", "");

                                            Call<Void> call3 = apiEducaEco.atualizarXp(email_aluno, xp);
                                            db.updateXp(email_aluno, xp);
                                            call3.enqueue(new Callback<Void>() {
                                                @Override
                                                public void onResponse(Call<Void> call, Response<Void> response) {
                                                    if (  response.isSuccessful() ) {
                                                        //Manda a notificação e atualiza o status da missão
                                                        String id_aluno = sharedPreferences.getString("id_aluno", "");
                                                        db.updateStatus(Long.parseLong(String.valueOf(id_aluno)), 1L);
                                                        int xpNovo = sharedPreferences.getInt("xp", 0) + xp;
                                                        SharedPreferences.Editor editor = getSharedPreferences("aluno", MODE_PRIVATE).edit();
                                                        editor.putInt("xp", xpNovo);
                                                        missaoVistoConcluido();

                                                        Log.e("Sucesso", "FUNCIONOUUU" + response.message());
                                                    } else {
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("tela", "aprenda");
                                                        Intent intent = new Intent(ActivityVideo.this, Main.class);
                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                        finish();
                                                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                                        Log.e("Erro", "ERROOO" + response.message());
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Void> call, Throwable t) {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("tela", "aprenda");
                                                    Intent intent = new Intent(ActivityVideo.this, Main.class);
                                                    intent.putExtras(bundle);
                                                    startActivity(intent);
                                                    finish();
                                                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                                    Log.e("Erro", t.getMessage());
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("tela", "aprenda");
                                    Intent intent = new Intent(ActivityVideo.this, Main.class);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                }
                            }

                            @Override
                            public void onFailure(Call<ContadorVideo> call, Throwable t) {
                                Bundle bundle = new Bundle();
                                bundle.putString("tela", "aprenda");
                                Intent intent = new Intent(ActivityVideo.this, Main.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                            }
                        });
                        Log.e("Sucesso", response.message());
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Bundle bundle = new Bundle();
                        bundle.putString("tela", "aprenda");
                        Intent intent = new Intent(ActivityVideo.this, Main.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

        Call<Void> call = api.incrementarContadorVideosVistos(email_aluno);

        call.enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                Call<ContadorVideo> call2 = api.verificarContadorVideosVistos(email_aluno);

                call2.enqueue(new retrofit2.Callback<ContadorVideo>() {
                    @Override
                    public void onResponse(Call<ContadorVideo> call, retrofit2.Response<ContadorVideo> response) {
                        ContadorVideo contadorVideo = response.body();
                        Integer contador = contadorVideo.getVideos_vistos();
                        if (  contador == 1 ) {
                            Database db = new Database();
                            db.getXPbyMissao(1, new XPCallback() {
                                @Override
                                public void onCallback(int xp) {
                                    String email_aluno = sharedPreferences.getString("email", "");

                                    Call<Void> call3 = apiEducaEco.atualizarXp(email_aluno, xp);
                                    db.updateXp(email_aluno, xp);
                                    call3.enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            if (  response.isSuccessful() ) {
                                                //Manda a notificação e atualiza o status da missão
                                                String id_aluno = sharedPreferences.getString("id_aluno", "");
                                                db.updateStatus(Long.parseLong(String.valueOf(id_aluno)), 1L);
                                                int xpNovo = sharedPreferences.getInt("xp", 0) + xp;
                                                SharedPreferences.Editor editor = getSharedPreferences("aluno", MODE_PRIVATE).edit();
                                                editor.putInt("xp", xpNovo);
                                                missaoVistoConcluido();
                                                Log.e("Sucesso", "FUNCIONOUUU" + response.message());
                                            } else {
                                                Log.e("Erro", "ERROOO" + response.message());
                                            }

                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Log.e("Erro", t.getMessage());
                                        }
                                    });
                                }
                            });
                        } else {
                            Log.e("Erro", "Erro" + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ContadorVideo> call, Throwable t) {
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

    public void missaoVistoConcluido() {
        String channelID = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelID);
        builder.setSmallIcon(R.drawable.logo_educaeco)
                .setContentTitle("EBAAAA! Você concluiu uma missão!")
                .setContentText("Receba 250xp por ter concluído a missão de Veja um vídeo na aba de aprenda")
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
