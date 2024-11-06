package com.mobile.educaeco.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.mobile.educaeco.NetworkUtil;
import com.mobile.educaeco.api.EducaEcoAPI;
import com.mobile.educaeco.fragments.AprendaFragment;
import com.mobile.educaeco.fragments.JogosFragment;
import com.mobile.educaeco.fragments.HomeFragment;
//import com.mobile.educaeco.fragments.Jogos;
import com.mobile.educaeco.R;
import com.mobile.educaeco.fragments.MissoesFragment;
import com.mobile.educaeco.fragments.PerfilFragment;
import com.mobile.educaeco.fragments.PraticaFragment;
import com.mobile.educaeco.fragments.QuizFragment;
import com.mobile.educaeco.fragments.VideosFragment;
import com.mobile.educaeco.models_api.Aluno;
import com.mobile.educaeco.models_api.Escola;
import com.mobile.educaeco.models_api.Professor;
import com.mobile.educaeco.models_api.Turma;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main extends AppCompatActivity {

    HomeFragment homeFragment;
    JogosFragment jogosFragment;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ImageView btnHome, btnJogos, btnMissoes, btnPerfil;
    TextView xp, nivel;
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Bundle bundle = getIntent().getExtras();

        homeFragment = new HomeFragment();
        jogosFragment = new JogosFragment();


        xp = findViewById(R.id.xp);
        nivel = findViewById(R.id.nivel);

        SharedPreferences sharedPreferences= getSharedPreferences("aluno", MODE_PRIVATE);

        xp.setText(String.valueOf(sharedPreferences.getInt("xp", 0) + "xp"));

        int nivelNumber = (sharedPreferences.getInt("xp", -1) / 1000);

        nivel.setText(String.valueOf(nivelNumber));


        if (bundle != null) {
            String tela = bundle.getString("tela");
            if (tela.equals("jogos")) {
                jogosFragment = new JogosFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameFrag, jogosFragment)
                        .commit();
            } else if ( tela.equals("aprenda") ) {
                AprendaFragment aprendaFragment = new AprendaFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameFrag, aprendaFragment)
                        .commit();
            } else if (tela.equals("quiz")) {
                QuizFragment quizFragment = new QuizFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameFrag, quizFragment)
                        .commit();
            } else if (tela.equals("pratica")) {
                PraticaFragment praticaFragment = new PraticaFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameFrag, praticaFragment)
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
                if ( NetworkUtil.isNetworkAvailable(getApplicationContext()) ) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frameFrag, jogosFragment);
                    fragmentTransaction.commit();
                } else {
                    showNoInternetToast();
                }
            }
        });

        btnMissoes = findViewById(R.id.btnMissoes);
        btnMissoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( NetworkUtil.isNetworkAvailable(getApplicationContext()) ) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frameFrag, new MissoesFragment())
                            .commit();
                } else {
                    showNoInternetToast();
                }
            }
        });

        btnPerfil = findViewById(R.id.btnPerfil);
        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( NetworkUtil.isNetworkAvailable(getApplicationContext()) ) {
                    PerfilFragment perfilFragment = new PerfilFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frameFrag, perfilFragment)
                            .commit();
                } else {
                    showNoInternetToast();
                }
            }
        });
    }

    private void showNoInternetToast() {
        Toast.makeText(getApplicationContext(), "Sem conexão com a internet. Verifique e tente novamente.", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        xpListener();
    }

    private void xpListener() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("aluno", MODE_PRIVATE);
        String id_aluno = sharedPreferences.getString("id_aluno", "");

        db.collection("alunos")
                .document(id_aluno)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Firestore", "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            // Obtenha o XP do documento
                            int novoXP = snapshot.getLong("xp").intValue();
                            // Chame a função para atualizar o XP na interface
                            atualizarXP(novoXP);
                        }
                    }
                });
    }


    private void atualizarXP(int novoXP) {
        xp.setText(String.valueOf(novoXP + "xp"));
        int nivelNumber = (novoXP / 1000);
        nivel.setText(String.valueOf(nivelNumber));
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