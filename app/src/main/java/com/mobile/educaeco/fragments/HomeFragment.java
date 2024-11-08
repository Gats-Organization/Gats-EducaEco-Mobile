package com.mobile.educaeco.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.mobile.educaeco.NetworkUtil;
import com.mobile.educaeco.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    AprendaFragment aprendaFragment;
    ImageView btnAprenda, btnRanking, btnQuiz, btnPratica;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment homeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Encontre o botão pelo ID
        btnAprenda = view.findViewById(R.id.btnAprenda);
        btnRanking = view.findViewById(R.id.btnRanking);
        btnQuiz = view.findViewById(R.id.btnQuiz);
        btnPratica = view.findViewById(R.id.btnPrática);

        // Definir o clique do botão para trocar de fragmento
        btnAprenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( NetworkUtil.isNetworkAvailable(getContext()) ) {
                    aprendaFragment = new AprendaFragment();
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.frameFrag, aprendaFragment)
                            .addToBackStack("aprenda")
                            .commit();
                } else {
                    showNoInternetToast();
                }
            }
        });

        btnPratica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( NetworkUtil.isNetworkAvailable(getContext()) ) {
                    PraticaFragment praticaFragment = new PraticaFragment();
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.frameFrag, praticaFragment)
                            .addToBackStack("pratica")
                            .commit();
                } else {
                    showNoInternetToast();
                }
            }
        });

        btnQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( NetworkUtil.isNetworkAvailable(getContext()) ) {
                    QuizFragment quizFragment = new QuizFragment();
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.frameFrag, quizFragment)
                            .addToBackStack("quiz")
                            .commit();
                } else {
                    showNoInternetToast();
                }
            }
        });

        btnRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( NetworkUtil.isNetworkAvailable(getContext()) ) {
                    RankingFragment rankingFragment = new RankingFragment();
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.frameFrag, rankingFragment)
                            .addToBackStack("ranking")
                            .commit();
                } else {
                    showNoInternetToast();
                }
            }
        });
    }

    private void showNoInternetToast() {
        Toast.makeText(getContext(), "Sem conexão com a internet. Verifique e tente novamente.", Toast.LENGTH_LONG).show();
    }
}