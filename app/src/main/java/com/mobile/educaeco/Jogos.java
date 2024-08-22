package com.mobile.educaeco;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class Jogos extends AppCompatActivity {

    ImageButton btnVolta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jogos);

        btnVolta = findViewById(R.id.voltar);
        btnVolta.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(Jogos.this, Home.class));
        });
    }
}