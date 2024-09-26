package com.mobile.educaeco.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mobile.educaeco.api.EducaEcoAPI;
import com.mobile.educaeco.fragments.AprendaFragment;
import com.mobile.educaeco.fragments.JogosFragment;
import com.mobile.educaeco.fragments.HomeFragment;
//import com.mobile.educaeco.fragments.Jogos;
import com.mobile.educaeco.R;
import com.mobile.educaeco.fragments.MissoesFragment;
import com.mobile.educaeco.fragments.PerfilFragment;
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
    private EducaEcoAPI api;
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        SharedPreferences sharedPreferences = getSharedPreferences("aluno", MODE_PRIVATE);
        String idAluno = sharedPreferences.getString("id_aluno", "");

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Bundle bundle = getIntent().getExtras();

        pegarInformaçõesAluno(idAluno, editor, sharedPreferences);

        homeFragment = new HomeFragment();
        jogosFragment = new JogosFragment();


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

        btnMissoes = findViewById(R.id.btnMissoes);
        btnMissoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameFrag, new MissoesFragment())
                        .commit();
            }
        });

        btnPerfil = findViewById(R.id.btnPerfil);
        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerfilFragment perfilFragment = new PerfilFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameFrag, perfilFragment)
                        .commit();
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

    public void pegarInformaçõesAluno(String idAluno, SharedPreferences.Editor editor, SharedPreferences sharedPreferences) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gats-repository-api.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(EducaEcoAPI.class);

        Call<Aluno> call = api.getAluno(idAluno);
        call.enqueue(new Callback<Aluno>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Aluno> call, Response<Aluno> response) {
                Aluno aluno = response.body();

                if (aluno != null) {
                    editor.putString("nome", aluno.getNome() + " " + aluno.getSobrenome());
                    editor.putString("email", aluno.getEmail());

                    Turma turma = aluno.getTurma();
                    editor.putString("turma", turma.getSerie() + " ano " + turma.getNomenclatura());
                    Escola escola = turma.getEscola();
                    editor.putString("escola", escola.getNome());
                    Professor professor = turma.getProfessor();
                    editor.putString("professor", professor.getNome());
                    editor.putInt("xp", aluno.getXp());
                    editor.commit();

                    xp = findViewById(R.id.xp);
                    nivel = findViewById(R.id.nivel);

                    xp.setText(String.valueOf(aluno.getXp() + "xp"));

                    int nivelNumber = (sharedPreferences.getInt("xp", -1) / 100) + 1;

                    nivel.setText(String.valueOf(nivelNumber));
                } else {
                    Toast.makeText(Main.this, "Aluno não encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Aluno> call, Throwable t) {
                Toast.makeText(Main.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}