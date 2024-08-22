package com.mobile.educaeco;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class Home extends AppCompatActivity {
    ImageView btnJogos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        btnJogos = findViewById(R.id.btnJogos);
        btnJogos.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(Home.this, Jogos.class));
        });
    }
}