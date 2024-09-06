package com.mobile.educaeco.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.mobile.educaeco.fragments.AprendaFragment;
import com.mobile.educaeco.fragments.JogosFragment;
import com.mobile.educaeco.fragments.HomeFragment;
//import com.mobile.educaeco.fragments.Jogos;
import com.mobile.educaeco.R;
import com.mobile.educaeco.fragments.VideosFragment;

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

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String tela = bundle.getString("tela");
            if (tela.equals("jogos")) {
                jogosFragment = new JogosFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameFrag, jogosFragment)
                        .commit();
            } else if ( tela.equals("video") ) {
                AprendaFragment aprendaFragment = new AprendaFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameFrag, aprendaFragment)
                        .commit();
            }
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameFrag, homeFragment).commit();
        }

        btnHome = findViewById(R.id.btnHome);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameFrag, homeFragment).commit();
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

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frameFrag);

        if (currentFragment instanceof JogosFragment) {
            fragmentManager.beginTransaction()
                    .replace(R.id.frameFrag, new HomeFragment())
                    .addToBackStack(null)
                    .commit();
        } else {
            super.onBackPressed();
        }
    }
}