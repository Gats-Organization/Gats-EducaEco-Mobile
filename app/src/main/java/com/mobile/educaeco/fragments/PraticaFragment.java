package com.mobile.educaeco.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.mobile.educaeco.activities.Camera;
import com.mobile.educaeco.adapters.AdapterPratica;
import com.mobile.educaeco.interfaces.PraticaCallback;
import com.mobile.educaeco.models.Pratica;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PraticaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PraticaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    List<Pratica> listaPratica = new ArrayList<>();
    Uri photoURI;
    RecyclerView recyclerPraticas;
    AdapterPratica adapterPraticas;
    Database db;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private static final long TIMEOUT_DURATION = 5000; // Tempo limite em milissegundos (10 segundos)
    private Handler timeoutHandler;
    private Runnable timeoutRunnable;
    public PraticaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PraticaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PraticaFragment newInstance(String param1, String param2) {
        PraticaFragment fragment = new PraticaFragment();
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
        return inflater.inflate(R.layout.fragment_pratica, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar o ActivityResultLauncher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            // Aqui você pode iniciar a CameraActivity e passar a URI da imagem
                            Intent cameraIntent = new Intent(getContext(), Camera.class);
                            cameraIntent.putExtra("imageUri", imageUri.toString());
                            cameraIntent.putExtra("ação", "galeria");
                            startActivity(cameraIntent);
                        }
                    }
                }
        );

        ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Não tente obter a URI do Intent, pois ela já está armazenada em photoURI
                        if (photoURI != null) {
                            Intent cameraIntent = new Intent(getContext(), Camera.class);
                            cameraIntent.putExtra("imageUri", photoURI.toString());
                            cameraIntent.putExtra("ação", "tirarfoto");
                            startActivity(cameraIntent);
                        } else {
                            Log.e("Camera", "photoURI is null");
                        }
                    }
                }
        );


        ImageView btnVoltar = view.findViewById(R.id.voltar);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager() != null && getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                }
            }
        });

        ImageView avisoEsperePratica = view.findViewById(R.id.avisoEsperePratica);
        avisoEsperePratica.setVisibility(View.GONE);

        ProgressBar loading = view.findViewById(R.id.load);

        loading.setVisibility(View.VISIBLE);

        timeoutHandler = new Handler();
        timeoutRunnable = () -> {
            loading.setVisibility(View.GONE);
            avisoEsperePratica.setVisibility(View.VISIBLE);
            Log.e("Timeout", "Tempo limite atingido para carregar quizzes");
        };
        timeoutHandler.postDelayed(timeoutRunnable, TIMEOUT_DURATION);

        recyclerPraticas = view.findViewById(R.id.listaPraticas);

        adapterPraticas = new AdapterPratica(new ArrayList<>(), galleryLauncher, cameraLauncher);
        recyclerPraticas.setLayoutManager(new LinearLayoutManager(getContext()));

        db = new Database();

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("aluno", MODE_PRIVATE);
        String id_aluno = sharedPreferences.getString("id_aluno", "");

        db.getPraticas(id_aluno, listaPratica, adapterPraticas, new PraticaCallback() {
            @Override
            public void onSuccess(List<Pratica> listaPratica) {
                timeoutHandler.removeCallbacks(timeoutRunnable);
                loading.setVisibility(View.GONE);

                adapterPraticas = new AdapterPratica(listaPratica, galleryLauncher, cameraLauncher);
                recyclerPraticas.setAdapter(adapterPraticas);
            }

            @Override
            public void onFailure(Exception e, List<Pratica> listaPratica) {
                timeoutHandler.removeCallbacks(timeoutRunnable);
                loading.setVisibility(View.GONE);
            }
        });

        recyclerPraticas.setAdapter(adapterPraticas);
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