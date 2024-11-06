package com.mobile.educaeco.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mobile.educaeco.Database;
import com.mobile.educaeco.R;
import com.mobile.educaeco.adapters.AdapterQuiz;
import com.mobile.educaeco.interfaces.QuizCallback;
import com.mobile.educaeco.models.Quiz;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuizFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Database db = new Database();
    RecyclerView recyclerQuiz;
    AdapterQuiz adapterQuiz;
    List<Quiz> listaQuiz = new ArrayList<>();

    private static final long TIMEOUT_DURATION = 15000;
    private Handler timeoutHandler;
    private Runnable timeoutRunnable;

    public QuizFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuizFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QuizFragment newInstance(String param1, String param2) {
        QuizFragment fragment = new QuizFragment();
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
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView btnVoltar = view.findViewById(R.id.voltar);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager() != null && getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                }
            }
        });

        recyclerQuiz = view.findViewById(R.id.listaQuiz);
        ProgressBar loading = view.findViewById(R.id.load);
        ImageView avisoNaoAchouQuiz = view.findViewById(R.id.avisoNaoAchouQuiz);
        ImageView avisoEspereQuiz = view.findViewById(R.id.avisoEspereQuiz);

        avisoNaoAchouQuiz.setVisibility(View.GONE);
        avisoEspereQuiz.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);

        adapterQuiz = new AdapterQuiz(listaQuiz, getContext(), getActivity().getSupportFragmentManager());
        recyclerQuiz.setLayoutManager(new LinearLayoutManager(getContext()));

        db = new Database();

        timeoutHandler = new Handler();
        timeoutRunnable = () -> {
            loading.setVisibility(View.GONE);
            avisoEspereQuiz.setVisibility(View.VISIBLE);
            Log.e("Timeout", "Tempo limite atingido para carregar quizzes");
        };
        timeoutHandler.postDelayed(timeoutRunnable, TIMEOUT_DURATION);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("aluno", MODE_PRIVATE);
        String id_aluno = sharedPreferences.getString("id_aluno", "");
        db.getQuizzes(String.valueOf(Long.parseLong(id_aluno)), listaQuiz, adapterQuiz, new QuizCallback() {

            @Override
            public void onSuccess(List<Quiz> quizzes) {
                timeoutHandler.removeCallbacks(timeoutRunnable);
                loading.setVisibility(View.GONE);
                avisoEspereQuiz.setVisibility(View.GONE);

                Log.d("Sucesso", quizzes.toString());

                adapterQuiz = new AdapterQuiz(quizzes, getContext(), getActivity().getSupportFragmentManager());
                recyclerQuiz.setAdapter(adapterQuiz);
            }

            @Override
            public void onFailure(Exception e, List<Quiz> quizzes) {
                timeoutHandler.removeCallbacks(timeoutRunnable);
                loading.setVisibility(View.GONE);
                if ( quizzes.isEmpty() ) {
                    avisoEspereQuiz.setVisibility(View.VISIBLE);
                } else {
                    avisoNaoAchouQuiz.setVisibility(View.VISIBLE);
                }
                Log.e("Error", "Não deu certo: Callback");
            }
        });

        recyclerQuiz.setAdapter(adapterQuiz);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Certifique-se de remover qualquer callback quando a visão for destruída
        if (timeoutHandler != null) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
        }
    }
}