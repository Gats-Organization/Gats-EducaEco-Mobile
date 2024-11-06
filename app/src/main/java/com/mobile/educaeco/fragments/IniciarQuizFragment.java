package com.mobile.educaeco.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.educaeco.R;
import com.mobile.educaeco.activities.Quiz;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IniciarQuizFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IniciarQuizFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public IniciarQuizFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IniciarQuizFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IniciarQuizFragment newInstance(String param1, String param2) {
        IniciarQuizFragment fragment = new IniciarQuizFragment();
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
        return inflater.inflate(R.layout.fragment_iniciar_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView btnVoltar = view.findViewById(R.id.voltar);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuizFragment quizFragment = new QuizFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction().
                        replace(R.id.frameFrag, quizFragment)
                        .commit();
            }
        });


        String temaQuizString = "";
        int quantQuestoesInt = 0;
        String idQuiz = "";

        Bundle bundle = getArguments();
        if (bundle != null) {
            // Obtenha os valores dos argumentos passados na chamada do fragment
            idQuiz = bundle.getString("idQuiz");
            temaQuizString = bundle.getString("temaQuiz");
            quantQuestoesInt = bundle.getInt("quantQuestoes");

            TextView temaQuiz = view.findViewById(R.id.temaIniciarQuiz);
            temaQuiz.setText(temaQuizString);

            TextView quantQuestoesQuiz = view.findViewById(R.id.quantQuestoesIniciarQuiz);
            quantQuestoesQuiz.setText(String.valueOf(quantQuestoesInt) + " questões");
        }

        ImageView btnIniciarQuiz = view.findViewById(R.id.btnIniciarQuiz);

        String finalTemaQuizString = temaQuizString;
        int finalQuantQuestoesInt = quantQuestoesInt;
        String finalIdQuiz = idQuiz;
        btnIniciarQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Quiz.class);
                Bundle bundle = new Bundle();
                bundle.putString("idQuiz", finalIdQuiz);
                bundle.putString("temaQuiz", finalTemaQuizString);
                bundle.putInt("quantQuestoes", finalQuantQuestoesInt);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}