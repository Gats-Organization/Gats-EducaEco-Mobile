package com.mobile.educaeco.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mobile.educaeco.Database;
import com.mobile.educaeco.R;
import com.mobile.educaeco.adapters.AdapterOpcoes;
import com.mobile.educaeco.interfaces.PerguntasCallback;
import com.mobile.educaeco.interfaces.QuizDBCallback;
import com.mobile.educaeco.models.Pergunta;
import com.mobile.educaeco.models.QuizDB;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Quiz extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView progressoQuiz;
    private TextView perguntaTextView;
    private List<Pergunta> listaPerguntas = new ArrayList<>();
    private int perguntaAtualIndex = 0; // Índice da pergunta atual
    private AdapterOpcoes adapterOpcoes;

    private double nota = 0;
    private int totalAcertos = 0;
    private long tempoInicio;
    private long tempoTermino;
    String temaQuiz = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Inicializando Views
        recyclerView = findViewById(R.id.listaOpcoes);
        progressoQuiz = findViewById(R.id.questoesCertas);
        perguntaTextView = findViewById(R.id.pergunta);

        ProgressBar loading = findViewById(R.id.load);
        ImageView imageQuiz = findViewById(R.id.imageView);

        loading.setVisibility(View.VISIBLE);
        imageQuiz.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterOpcoes = new AdapterOpcoes(new ArrayList<>(), null, -1); // Inicialize com uma lista vazia
        recyclerView.setAdapter(adapterOpcoes); // Defina o adaptador aqui

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            temaQuiz = bundle.getString("temaQuiz");
        }

        Database db = new Database();

        tempoInicio = System.currentTimeMillis();

        // Buscando as IDs das perguntas pelo tema
        db.getQuizIdsPerguntasByTema(temaQuiz, new QuizDBCallback() {
            @Override
            public void onComplete(QuizDB quizDB) {
                if (quizDB.getTema() != null && quizDB.getPerguntasIds() != null) {
                    List<String> perguntasIds = quizDB.getPerguntasIds();

                    // Buscando as perguntas com base nos IDs
                    db.getPerguntas(perguntasIds, new PerguntasCallback() {
                        @Override
                        public void onComplete(List<Pergunta> perguntas) {
                            runOnUiThread(() -> {
                                loading.setVisibility(View.GONE);
                                imageQuiz.setVisibility(View.VISIBLE);
                                listaPerguntas.addAll(perguntas);
                                exibirPergunta();
                                Log.d("Quiz", "Perguntas: " + perguntas);
                            });
                        }
                    });
                }
            }
        });
    }

    private void exibirPergunta() {
        runOnUiThread(() -> {
            if (perguntaAtualIndex < listaPerguntas.size()) {
                TextView proximaEtapa = findViewById(R.id.proximaEtapa);
                proximaEtapa.setVisibility(View.GONE);

                Pergunta perguntaAtual = listaPerguntas.get(perguntaAtualIndex);
                perguntaTextView.setText(perguntaAtual.getPergunta());
                progressoQuiz.setText((perguntaAtualIndex + 1) + "/" + listaPerguntas.size());
                Log.d("Quiz", "Pergunta atual: " + perguntaAtual.getPergunta());

                List<String> opcoes = perguntaAtual.getOpcoes();
                int opcaoCorretaIndex = opcoes.indexOf(perguntaAtual.getOpcao_correta());

                adapterOpcoes = new AdapterOpcoes(opcoes, opcaoSelecionada -> {
                    proximaEtapa.setText("Responder");
                    proximaEtapa.setVisibility(View.VISIBLE);

                    proximaEtapa.setOnClickListener(v -> {
                        if ("Responder".equals(proximaEtapa.getText().toString())) {
                            // Checar resposta e atualizar cores
                            adapterOpcoes.checarResposta();
                            if (perguntaAtualIndex < listaPerguntas.size() - 1) {
                                proximaEtapa.setText("Continuar");
                            } else {
                                proximaEtapa.setText("Finalizar");
                            }
                        } else {
                            // Avançar para a próxima pergunta
                            verificarResposta(opcaoSelecionada);
                        }
                    });
                }, opcaoCorretaIndex);


                recyclerView.setAdapter(adapterOpcoes);
            }
        });
    }


    private void verificarResposta(String opcaoSelecionada) {
        Pergunta perguntaAtual = listaPerguntas.get(perguntaAtualIndex);
        TextView proximaEtapa = findViewById(R.id.proximaEtapa);

        // Lógica para conferir se a resposta está correta
        if (opcaoSelecionada.equals(perguntaAtual.getOpcao_correta())) {
            double valorQuestao = 10.0 / listaPerguntas.size();
            nota += valorQuestao;
            totalAcertos++;
        }

        // Avançar para a próxima pergunta
        perguntaAtualIndex++;

        if (perguntaAtualIndex < listaPerguntas.size()) {
            exibirPergunta();
            proximaEtapa.setText("Continuar");
        } else {
            finalizarQuiz();
        }
    }

    private void finalizarQuiz() {
        tempoTermino = System.currentTimeMillis();
        long duracaoMilisegundos = tempoTermino - tempoInicio;
        long duracaoSegundos = duracaoMilisegundos / 1000;
        long min = duracaoSegundos / 60;
        long seg = duracaoSegundos % 60;

        SharedPreferences sharedPreferences = getSharedPreferences("aluno", MODE_PRIVATE);
        String idAluno = sharedPreferences.getString("id_aluno", "");

        Bundle bundleCapture = getIntent().getExtras();
        String idQuiz = (String) bundleCapture.get("idQuiz");

        Map<String, Object> resultadoQuiz = new HashMap<>();
        resultadoQuiz.put("id_aluno", Integer.parseInt(idAluno));

        resultadoQuiz.put("id_quiz", idQuiz);
        resultadoQuiz.put("nota", nota);
        resultadoQuiz.put("quantAcertos", totalAcertos);
        resultadoQuiz.put("totalPerguntas", listaPerguntas.size());
        resultadoQuiz.put("duracao", min + "min " + seg + "segs");
        resultadoQuiz.put("dataConclusao", new Date());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("controle_quiz")
                .document(idQuiz + "_" + idAluno) // Documento único para cada quiz/aluno
                .set(resultadoQuiz)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Quiz", "Resultado do quiz salvo com sucesso!");

                    // Continua para a tela de resultados
                    Bundle bundle = new Bundle();
                    bundle.putDouble("nota", nota);
                    bundle.putInt("totalAcertos", totalAcertos);
                    bundle.putInt("totalErros", listaPerguntas.size() - totalAcertos);
                    bundle.putInt("totalQuestoes", listaPerguntas.size());
                    bundle.putString("duracao", min + "min " + seg + "segs");
                    bundle.putString("temaQuiz", temaQuiz);
                    bundle.putBoolean("incrementar", true);

                    Intent intent = new Intent(Quiz.this, ResultadoQuiz.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w("Quiz", "Erro ao salvar o resultado do quiz", e);
                    Toast.makeText(this, "Erro ao salvar resultado. Tente novamente.", Toast.LENGTH_SHORT).show();
                });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {;
        Log.d("Quiz", "Proibido sair sem completar o quiz");
    }

    @Override
    protected void onUserLeaveHint() {
        Log.d("Quiz", "Proibido sair sem completar o quiz");
    }
}
