package com.mobile.educaeco.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mobile.educaeco.Database;
import com.mobile.educaeco.R;
import com.mobile.educaeco.api.ContadorAPI;
import com.mobile.educaeco.api.EducaEcoAPI;
import com.mobile.educaeco.interfaces.XPCallback;
import com.mobile.educaeco.models_api.ContadorJogo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JogoLixoZero extends AppCompatActivity {

    WebView webView;
    ProgressBar load;
    ImageView fechar;
    ContadorAPI api;
    EducaEcoAPI apiEducaEco;
    SharedPreferences sharedPreferences;
    @SuppressLint({"SetJavaScriptEnabled", "MissingInflatedId", "RestrictedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jogolixozero);

        webView = findViewById(R.id.game);
        load = findViewById(R.id.loadbar);
        fechar = findViewById(R.id.fechar);

        webView.loadUrl("https://scratch.mit.edu/projects/1082750543/fullscreen/");
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());

        sharedPreferences = getSharedPreferences("aluno", MODE_PRIVATE);

        Bundle bundle = new Bundle();
        bundle.putString("tela", "jogos");


        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if ( ContextCompat.checkSelfPermission(JogoLixoZero.this,
                    android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        JogoLixoZero.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        fechar.setOnClickListener(new View.OnClickListener() {
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

                String email_aluno = sharedPreferences.getString("email", "");

                Call<Void> call = api.incrementarContadorVezesJogadas(email_aluno);

                call.enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                        Call<ContadorJogo> call2 = api.verificarContadorVezesJogadas(email_aluno);

                        call2.enqueue(new retrofit2.Callback<ContadorJogo>() {
                            @Override
                            public void onResponse(Call<ContadorJogo> call, retrofit2.Response<ContadorJogo> response) {
                                ContadorJogo contadorJogo = response.body();
                                Integer contador = contadorJogo.getVezes_jogadas();
                                if (  contador == 1 ) {
                                    Database db = new Database();
                                    db.getXPbyMissao(2, new XPCallback() {
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
                                                        db.updateStatus(Long.parseLong(String.valueOf(id_aluno)), 2L);
                                                        int xpNovo = sharedPreferences.getInt("xp", 0) + xp;
                                                        SharedPreferences.Editor editor = getSharedPreferences("aluno", MODE_PRIVATE).edit();
                                                        editor.putInt("xp", xpNovo);
                                                        missaoJogosConcluida(); // Função que manda a notificação
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("tela", "jogos");
                                                        Intent intent = new Intent(JogoLixoZero.this, Main.class);
                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                        finish();
                                                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                                        Log.e("Sucesso", "FUNCIONOUUU" + response.message());
                                                    } else {
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("tela", "jogos");
                                                        Intent intent = new Intent(JogoLixoZero.this, Main.class);
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
                                                    bundle.putString("tela", "jogos");
                                                    Intent intent = new Intent(JogoLixoZero.this, Main.class);
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
                                    bundle.putString("tela", "jogos");
                                    Intent intent = new Intent(JogoLixoZero.this, Main.class);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                }
                            }

                            @Override
                            public void onFailure(Call<ContadorJogo> call, Throwable t) {
                                Bundle bundle = new Bundle();
                                bundle.putString("tela", "jogos");
                                Intent intent = new Intent(JogoLixoZero.this, Main.class);
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
                        bundle.putString("tela", "jogos");
                        Intent intent = new Intent(JogoLixoZero.this, Main.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                });
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                load.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                load.setVisibility(View.INVISIBLE);
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack() ) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

        Call<Void> call = api.incrementarContadorVezesJogadas(email_aluno);

        call.enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                Call<ContadorJogo> call2 = api.verificarContadorVezesJogadas(email_aluno);

                call2.enqueue(new retrofit2.Callback<ContadorJogo>() {
                    @Override
                    public void onResponse(Call<ContadorJogo> call, retrofit2.Response<ContadorJogo> response) {
                        ContadorJogo contadorJogo = response.body();
                        Integer contador = contadorJogo.getVezes_jogadas();
                        if (  contador == 1 ) {
                            Database db = new Database();
                            db.getXPbyMissao(2, new XPCallback() {
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
                                                db.updateStatus(Long.parseLong(String.valueOf(id_aluno)), 2L);
                                                int xpNovo = sharedPreferences.getInt("xp", 0) + xp;
                                                SharedPreferences.Editor editor = getSharedPreferences("aluno", MODE_PRIVATE).edit();
                                                editor.putInt("xp", xpNovo);
                                                missaoJogosConcluida(); // Função que manda a notificação
                                                Bundle bundle = new Bundle();
                                                bundle.putString("tela", "jogos");
                                                Intent intent = new Intent(JogoLixoZero.this, Main.class);
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                                finish();
                                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                                Log.e("Sucesso", "FUNCIONOUUU" + response.message());
                                            } else {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("tela", "jogos");
                                                Intent intent = new Intent(JogoLixoZero.this, Main.class);
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
                                            bundle.putString("tela", "jogos");
                                            Intent intent = new Intent(JogoLixoZero.this, Main.class);
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
                            bundle.putString("tela", "jogos");
                            Intent intent = new Intent(JogoLixoZero.this, Main.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        }
                    }

                    @Override
                    public void onFailure(Call<ContadorJogo> call, Throwable t) {
                        Bundle bundle = new Bundle();
                        bundle.putString("tela", "jogos");
                        Intent intent = new Intent(JogoLixoZero.this, Main.class);
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
                bundle.putString("tela", "jogos");
                Intent intent = new Intent(JogoLixoZero.this, Main.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    public void missaoJogosConcluida() {
        String channelID = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelID);
        builder.setSmallIcon(R.drawable.logo_educaeco)
                .setContentTitle("EBAAAA! Você concluiu uma missão!")
                .setContentText("Receba 250xp por ter concluído a missão de Jogue o jogo Lixo Zero")
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