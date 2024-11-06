package com.mobile.educaeco.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.educaeco.R;
import com.mobile.educaeco.api.EducaEcoAPI;
import com.mobile.educaeco.models_api.Aluno;
import com.mobile.educaeco.models_api.Turma;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RankingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RankingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    EducaEcoAPI api;

    public RankingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RankingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RankingFragment newInstance(String param1, String param2) {
        RankingFragment fragment = new RankingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ( getArguments() != null ) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ranking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProgressBar load = view.findViewById(R.id.load);

        ConstraintLayout card = view.findViewById(R.id.card_ranking);
        ImageView card1 = view.findViewById(R.id.card_primeiro);
        ImageView card2 = view.findViewById(R.id.card_segundo);
        ImageView card3 = view.findViewById(R.id.card_terceiro);
        ImageView nivel1 = view.findViewById(R.id.nivelprimeiro);
        ImageView nivel2 = view.findViewById(R.id.nivelsegundo);
        ImageView nivel3 = view.findViewById(R.id.nivelterceiro);
        ImageView aviso = view.findViewById(R.id.aviso_ranking);

        TextView turmaNome = view.findViewById(R.id.turma_ranking);
        TextView primeiro = view.findViewById(R.id.primeiro);
        TextView segundo = view.findViewById(R.id.segundo);
        TextView terceiro = view.findViewById(R.id.terceiro);
        TextView primeiroxp = view.findViewById(R.id.primeiroxp);
        TextView segundoxp = view.findViewById(R.id.segundoxp);
        TextView terceiroxp = view.findViewById(R.id.terceiroxp);
        TextView primeironivel = view.findViewById(R.id.primeironivel);
        TextView segundonivel = view.findViewById(R.id.segundonivel);
        TextView terceironivel = view.findViewById(R.id.terceironivel);
        TextView primeiropodio = view.findViewById(R.id.primeiropodio);
        TextView segundopodio = view.findViewById(R.id.segundopodio);
        TextView terceiropodio = view.findViewById(R.id.terceiropodio);

        load.setVisibility(View.VISIBLE);
        card1.setVisibility(View.INVISIBLE);
        card2.setVisibility(View.INVISIBLE);
        card3.setVisibility(View.INVISIBLE);
        nivel1.setVisibility(View.INVISIBLE);
        nivel2.setVisibility(View.INVISIBLE);
        nivel3.setVisibility(View.INVISIBLE);
        aviso.setVisibility(View.GONE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gats-educaeco-api-dev2-pe6e.onrender.com//")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(EducaEcoAPI.class);

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("aluno", 0);
        Call<Aluno> call = api.getAluno(sharedPreferences.getString("id_aluno", ""));

        Log.d("Ranking", sharedPreferences.getString("id_aluno", ""));

        call.enqueue(new Callback<Aluno>() {
            @Override
            public void onResponse(Call<Aluno> call, retrofit2.Response<Aluno> response) {
                if(response.isSuccessful()){
                    Aluno aluno = response.body();

                    Turma turma = aluno.getTurma();

                    Long idTurma = turma.getId();

                    Call<List<Aluno>> call2 = api.getAlunosByTurma(String.valueOf(idTurma));

                    call2.enqueue(new Callback<List<Aluno>>() {
                        @Override
                        public void onResponse(Call<List<Aluno>> call2, retrofit2.Response<List<Aluno>> response) {
                            if(response.isSuccessful()){
                                List<Aluno> alunos = response.body();
                                load.setVisibility(View.GONE);
                                if (alunos != null) {
                                    Log.d("Ranking", alunos.toString());
                                    turmaNome.setText("Ranking " + turma.getSerie() + " ano " + turma.getNomenclatura());


                                    if (isAdded() && getContext() != null) {
                                        card.setBackground(requireContext().getResources().getDrawable(R.drawable.card_ranking));
                                    }
                                    card1.setVisibility(View.VISIBLE);
                                    card2.setVisibility(View.VISIBLE);
                                    card3.setVisibility(View.VISIBLE);
                                    nivel1.setVisibility(View.VISIBLE);
                                    nivel2.setVisibility(View.VISIBLE);
                                    nivel3.setVisibility(View.VISIBLE);
                                    // Ordena a lista de alunos com base em 'xp' em ordem decrescente
                                    List<Aluno> alunosOrdenados = alunos.stream()
                                            .sorted((a1, a2) -> Integer.compare(a2.getXp(), a1.getXp())) // Ordem decrescente
                                            .collect(Collectors.toList());

                                    // Verifica se há pelo menos 3 alunos para evitar IndexOutOfBoundsException
                                    if (alunosOrdenados.size() >= 3) {
                                        // Obtenha os 3 primeiros alunos
                                        String nome1 = alunosOrdenados.get(0).getNome();
                                        String nome2 = alunosOrdenados.get(1).getNome();
                                        String nome3 = alunosOrdenados.get(2).getNome();

                                        primeiro.setText(nome1);
                                        primeiropodio.setText(nome1);
                                        primeiroxp.setText(String.valueOf(alunosOrdenados.get(0).getXp()) + "xp");
                                        primeironivel.setText(String.valueOf((alunosOrdenados.get(0).getXp() / 1000)));

                                        segundo.setText(nome2);
                                        segundopodio.setText(nome2);
                                        segundoxp.setText(String.valueOf(alunosOrdenados.get(1).getXp()) + "xp");
                                        segundonivel.setText(String.valueOf((alunosOrdenados.get(1).getXp() / 1000)));

                                        terceiro.setText(nome3);
                                        terceiropodio.setText(nome3);
                                        terceiroxp.setText(String.valueOf(alunosOrdenados.get(2).getXp()) + "xp");
                                        terceironivel.setText(String.valueOf((alunosOrdenados.get(2).getXp() / 1000)));
                                    } else {
                                        load.setVisibility(View.GONE);
                                        aviso.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    load.setVisibility(View.GONE);
                                    aviso.setVisibility(View.VISIBLE);
                                }
                            } else {
                                load.setVisibility(View.GONE);
                                aviso.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Aluno>> call2, Throwable t) {
                            load.setVisibility(View.GONE);
                            aviso.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    load.setVisibility(View.GONE);
                    aviso.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Aluno> call, Throwable t) {
                aviso.setVisibility(View.VISIBLE);
            }
        });



        ImageView btnVoltar = view.findViewById(R.id.voltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager() != null && getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                }
            }
        });
    }
}