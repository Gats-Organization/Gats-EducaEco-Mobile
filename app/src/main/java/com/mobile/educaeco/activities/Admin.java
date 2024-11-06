package com.mobile.educaeco.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.educaeco.R;

public class Admin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button btnForms = findViewById(R.id.btnForms);
        Button btnResultados = findViewById(R.id.btnResultados);
        Button btnDashBoard = findViewById(R.id.btnDashBoard);
        TextView logout = findViewById(R.id.logoutAdmin);

        btnForms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin.this, AdminForms.class);
                startActivity(intent);
            }
        });

        btnResultados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin.this, AdminResultados.class);
                startActivity(intent);
            }
        });

        btnDashBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin.this, AdminDashBoard.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isAdminLogged", false);
                editor.apply();
                Intent intent = new Intent(Admin.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

    }
}