package com.mobile.educaeco.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import com.google.firebase.firestore.FirebaseFirestore;
import com.mobile.educaeco.R;

public class JogoLixoZero extends AppCompatActivity {

    WebView webView;
    ProgressBar load;
    ImageView fechar;

    @SuppressLint({"SetJavaScriptEnabled", "MissingInflatedId", "RestrictedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jogolixozero);

        webView = findViewById(R.id.game);
        load = findViewById(R.id.loadbar);
        fechar = findViewById(R.id.fechar);

        webView.loadUrl("https://scratch.mit.edu/projects/1022847871/fullscreen/");
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());

        Bundle bundle = new Bundle();
        bundle.putString("tela", "jogos");


        if (  Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "channel_notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("aluno", MODE_PRIVATE);
        String id_aluno = sharedPreferences.getString("id_aluno", "");
        db.collection("controle_missoes")
                .whereEqualTo("id_aluno", id_aluno)
                .whereEqualTo("id_missao",2)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.getResult().isEmpty()) {
                        db.collection("controle_missoes")
                                .document(task.getResult().getDocuments().get(0).getId())
                                .update("status", true);
                        displayNotification("Parabéns você concluiu a missão!", "Você ganhou mais de 20xp");
                    }
                });

        fechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("tela", "jogos");
                Intent intent = new Intent(JogoLixoZero.this, Main.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
        Bundle bundle = new Bundle();
        bundle.putString("tela", "jogos");
        Intent intent = new Intent(JogoLixoZero.this, Main.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private static final String CHANNEL_ID = "channel_id";
    private static final String CHANNEL_NAME = "channel_notification";
    private static final String CHANNEL_DESCRIPTION = "channel_description";

    public void displayNotification(String titulo, String mensagem) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_educaeco)
                .setContentTitle(titulo)
                .setContentText(mensagem)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if ( ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED ) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }
}