package com.mobile.educaeco.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.educaeco.Database;
import com.mobile.educaeco.NetworkUtil;
import com.mobile.educaeco.R;
import com.mobile.educaeco.activities.ResultadoQuiz;
import com.mobile.educaeco.fragments.IniciarQuizFragment;
import com.mobile.educaeco.fragments.RankingFragment;
import com.mobile.educaeco.interfaces.ControleQuizCallback;
import com.mobile.educaeco.models.ControleQuiz;
import com.mobile.educaeco.models.Quiz;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.List;

public class AdapterQuiz extends RecyclerView.Adapter<AdapterQuiz.ViewHolder> {

    private List<Quiz> listaQuiz;
    private Context context;
    private FragmentManager fragmentManager;

    // Atualize o construtor para receber o FragmentManager
    public AdapterQuiz(List<Quiz> listaQuiz, Context context, FragmentManager fragmentManager) {
        this.listaQuiz = listaQuiz;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public AdapterQuiz.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_quiz, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterQuiz.ViewHolder holder, int position) {
        Quiz quiz = listaQuiz.get(position);

        holder.temaQuiz.setText(quiz.getTema());
        holder.quantQuestoesQuiz.setText(quiz.getPerguntasIds().size() + " Questões");
        holder.dataEntregaQuiz.setText("Data de Entrega: " + new SimpleDateFormat("dd/MM/yyyy hh:mm").format(quiz.getDataFinalizacao()));

        // Defina o clique no ViewHolder
        holder.bind(quiz);
    }

    @Override
    public int getItemCount() {
        return listaQuiz.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView temaQuiz;
        public TextView quantQuestoesQuiz;
        public TextView dataEntregaQuiz;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            temaQuiz = itemView.findViewById(R.id.temaQuiz);
            quantQuestoesQuiz = itemView.findViewById(R.id.quantQuestoesQuiz);
            dataEntregaQuiz = itemView.findViewById(R.id.dataEntregaQuiz);
        }

        public void bind(Quiz quiz) {
            itemView.setOnClickListener(v -> {
                SharedPreferences sharedPreferences = context.getSharedPreferences("aluno", Context.MODE_PRIVATE);
                String id_aluno = sharedPreferences.getString("id_aluno", "");

                Database db = new Database();

                if(!id_aluno.equals("")) {
                    db.verificarRegistroControleQuiz(id_aluno,quiz.getId(), new ControleQuizCallback() {

                        @Override
                        public void onCompleted(ControleQuiz controleQuiz) {
                            Intent intent = new Intent(context, ResultadoQuiz.class);
                            double notaDouble = controleQuiz.getNota();
                            int certasInt = controleQuiz.getQuantAcertos();
                            int totalQuestoes = quiz.getPerguntasIds().size();
                            String tempoDuracao = controleQuiz.getDuracao();
                            String temaQuiz = quiz.getTema();

                            Bundle bundle = new Bundle();
                            bundle.putDouble("nota", notaDouble);
                            bundle.putInt("totalAcertos", certasInt);
                            bundle.putInt("totalQuestoes", totalQuestoes);
                            bundle.putString("duracao", tempoDuracao);
                            bundle.putString("temaQuiz", temaQuiz);
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        }

                        @Override
                        public void onIncomplete() {
                            // Verifique se o FragmentManager não é nulo antes de iniciar a transação
                            if (fragmentManager != null) {
                                // Passar dados para o IniciarQuizFragment
                                IniciarQuizFragment fragmentIniciarQuiz = new IniciarQuizFragment();
                                Bundle bundle = new Bundle();
                                bundle.putInt("quantQuestoes", quiz.getPerguntasIds().size());
                                bundle.putString("temaQuiz", quiz.getTema());
                                bundle.putString("idQuiz", quiz.getId());
                                fragmentIniciarQuiz.setArguments(bundle);

                                if ( NetworkUtil.isNetworkAvailable(context) ) {
                                    // Realizar a transação para substituir o fragment atual
                                    fragmentManager.beginTransaction()
                                            .replace(R.id.frameFrag, fragmentIniciarQuiz)
                                            .addToBackStack(null)
                                            .commit();
                                } else {
                                    showNoInternetToast();
                                }
                            } else {
                                Toast.makeText(context, "Não foi possível abrir o quiz", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(context, "Não foi possível encontrar o aluno", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(context, "Não foi possível encontrar o aluno", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void showNoInternetToast() {
        Toast.makeText(context, "Sem conexão com a internet. Verifique e tente novamente.", Toast.LENGTH_LONG).show();
    }
}
