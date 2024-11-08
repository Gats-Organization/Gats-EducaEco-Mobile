package com.mobile.educaeco.fragments;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import com.mobile.educaeco.activities.JogoLixoZero;
import com.mobile.educaeco.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JogosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JogosFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImageView jogoLixoZero;
    ImageView btnVoltar;

    public JogosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JogosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JogosFragment newInstance(String param1, String param2) {
        JogosFragment fragment = new JogosFragment();
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
        return inflater.inflate(R.layout.fragment_jogos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Encontre o botão pelo ID
        jogoLixoZero = view.findViewById(R.id.jogoLixoZero);

        // Definir o clique do botão para trocar de fragmento
        jogoLixoZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( NetworkUtil.isNetworkAvailable(getContext()) ) {
                    requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    Intent intent = new Intent(getActivity(), JogoLixoZero.class);
                    startActivity(intent);
                    requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    showNoInternetToast();
                }
            }
        });

        btnVoltar = view.findViewById(R.id.voltar);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment homeFragment = new HomeFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameFrag, homeFragment)
                        .addToBackStack("home")
                        .commit();
            }
        });
    }

    private void showNoInternetToast() {
        Toast.makeText(getContext(), "Sem conexão com a internet. Verifique e tente novamente.", Toast.LENGTH_LONG).show();
    }
}