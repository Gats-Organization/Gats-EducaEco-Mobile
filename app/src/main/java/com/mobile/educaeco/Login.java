package com.mobile.educaeco;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {
    ImageView imgLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        imgLogin = findViewById(R.id.btnLogin);

        imgLogin.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Home.class);
            startActivity(intent);
            finish();
        });
    }
}
