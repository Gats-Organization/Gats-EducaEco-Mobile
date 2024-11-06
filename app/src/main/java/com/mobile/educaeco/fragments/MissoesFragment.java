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
import com.mobile.educaeco.adapters.AdapterMissao;
import com.mobile.educaeco.interfaces.MissoesCallback;
import com.mobile.educaeco.models.Missao;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MissoesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MissoesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerMissoes;
    AdapterMissao adapterMissoes;
    List<Missao> listaMissoes = new ArrayList<>();
    Database db = new Database();
    private static final long TIMEOUT_DURATION = 5000; // Tempo limite em milissegundos (10 segundos)
    private Handler timeoutHandler;
    private Runnable timeoutRunnable;

    public MissoesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MissoesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MissoesFragment newInstance(String param1, String param2) {
        MissoesFragment fragment = new MissoesFragment();
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
        return inflater.inflate(R.layout.fragment_missoes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView btnVoltar = view.findViewById(R.id.voltar);

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

        ProgressBar loading = view.findViewById(R.id.load);
        ImageView missao_recado = view.findViewById(R.id.missao_concluida);

        loading.setVisibility(View.VISIBLE);
        missao_recado.setVisibility(View.INVISIBLE);

        recyclerMissoes = view.findViewById(R.id.listaPraticas);

        adapterMissoes = new AdapterMissao(new ArrayList<>());
        recyclerMissoes.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("aluno", MODE_PRIVATE);
        String idAluno = sharedPreferences.getString("id_aluno", "");

        recyclerMissoes.setAdapter(adapterMissoes);

        timeoutHandler = new Handler();
        timeoutRunnable = () -> {
            loading.setVisibility(View.GONE);
            missao_recado.setVisibility(View.VISIBLE);
            Log.e("Timeout", "Tempo limite atingido para carregar as missões");
        };
        timeoutHandler.postDelayed(timeoutRunnable, TIMEOUT_DURATION);

        db.getMissoesByIdAlunoStatus(idAluno, listaMissoes, adapterMissoes, new MissoesCallback() {
            @Override
            public void onCallback(List<Missao> missoes) {
                timeoutHandler.removeCallbacks(timeoutRunnable);
                Log.d("MissoesFragment", "Callback recebido com " + missoes.size() + " missões");

                loading.setVisibility(View.GONE);

                if ( missoes == null  ) {
                    missao_recado.setVisibility(View.VISIBLE);
                }

                adapterMissoes = new AdapterMissao(missoes);
                recyclerMissoes.setAdapter(adapterMissoes);
            }
        });
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