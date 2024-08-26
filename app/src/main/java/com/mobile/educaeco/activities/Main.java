package com.mobile.educaeco.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.mobile.educaeco.fragments.JogosFragment;
import com.mobile.educaeco.fragments.HomeFragment;
//import com.mobile.educaeco.fragments.Jogos;
import com.mobile.educaeco.R;

public class Main extends AppCompatActivity {

    HomeFragment homeFragment;
    JogosFragment jogosFragment;

    ImageView btnHome, btnJogos, btnMissoes, btnPerfil;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        homeFragment = new HomeFragment();
        jogosFragment = new JogosFragment();


        final FragmentTransaction[] home_frag = {getSupportFragmentManager().beginTransaction()};
        home_frag[0].add(R.id.frameFrag, homeFragment);
        home_frag[0].commit();

        btnHome = findViewById(R.id.btnHome);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home_frag[0] = getSupportFragmentManager().beginTransaction();
                home_frag[0].replace(R.id.frameFrag, homeFragment);
                home_frag[0].commit();
            }
        });

        btnJogos = findViewById(R.id.btnJogos);
        btnJogos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameFrag, jogosFragment);
                fragmentTransaction.commit();
            }
        });
    }
}