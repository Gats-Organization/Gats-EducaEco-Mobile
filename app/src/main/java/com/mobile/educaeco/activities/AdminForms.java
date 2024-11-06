package com.mobile.educaeco.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import com.mobile.educaeco.R;

public class AdminForms extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_forms);

        ImageView btnVoltar = findViewById(R.id.voltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminForms.this, Admin.class);
                startActivity(intent);
            }
        });

        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());

        // Habilita JavaScript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Carrega a URL
        webView.loadUrl("https://gats-educaeco-formularioia-add.onrender.com/");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
