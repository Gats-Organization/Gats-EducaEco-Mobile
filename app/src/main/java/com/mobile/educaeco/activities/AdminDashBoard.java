package com.mobile.educaeco.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.mobile.educaeco.R;

public class AdminDashBoard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash_board);

        ImageView btnVoltar = findViewById(R.id.voltar);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashBoard.this, Admin.class);
                startActivity(intent);
            }
        });

        // Configuração da WebView para carregar o iframe
        WebView webView = findViewById(R.id.dashboard);

        webView.setInitialScale(100);

        // Habilitar JavaScript para o conteúdo embutido
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true); // Habilitar armazenamento DOM, útil para alguns conteúdos web

        // HTML com o iframe
        String html = "<html><body>" +
                "<iframe title='Trabalho Interdisciplinar Admin - Gats The Aluno'" +
                "width='100%' height='100%' style='border: none;'" +
                "src='https://app.powerbi.com/view?r=eyJrIjoiNTRmMjBhZmQtODAxZi00MjE5LTkxMjEtMDg1ODhlOTQ1NjQ3IiwidCI6ImIxNDhmMTRjLTIzOTctNDAyYy1hYjZhLTFiNDcxMTE3N2FjMCJ9'" +
                "frameborder='0' allowFullScreen='true'></iframe>" +
                "</body></html>";

        // Carregar o HTML na WebView
        webView.loadData(html, "text/html", "UTF-8");
    }
}