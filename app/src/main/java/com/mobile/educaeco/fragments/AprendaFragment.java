package com.mobile.educaeco.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.educaeco.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AprendaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AprendaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImageView btnCincoRs, btnCidadeInteli, btnESG, btnRios, btnVoltar;

    public AprendaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AprendaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AprendaFragment newInstance(String param1, String param2) {
        AprendaFragment fragment = new AprendaFragment();
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
        return inflater.inflate(R.layout.fragment_aprenda, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = new Bundle();

        // Encontre o botão pelo ID
        btnCincoRs = view.findViewById(R.id.btnCincoRs);
        btnCidadeInteli = view.findViewById(R.id.btnCidadeInteli);
        btnESG = view.findViewById(R.id.btnESG);
        btnRios = view.findViewById(R.id.btnRios);
        btnVoltar = view.findViewById(R.id.voltar);

        // Definir o clique do botão para trocar de fragmento
        btnCincoRs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConteudosFragment conteudosFragment = new ConteudosFragment();

                bundle.putString("conteudo", "5 R's");
                conteudosFragment.setArguments(bundle);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameFrag, conteudosFragment)
                        .addToBackStack("conteudos")
                        .commit();
            }
        });

        btnCidadeInteli.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   ConteudosFragment conteudosFragment = new ConteudosFragment();

                   bundle.putString("conteudo", "Cidades Inteligentes");
                   conteudosFragment.setArguments(bundle);

                   getParentFragmentManager().beginTransaction()
                           .replace(R.id.frameFrag, conteudosFragment)
                           .addToBackStack("conteudos")
                           .commit();
               }
           }
        );

        btnESG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConteudosFragment conteudosFragment = new ConteudosFragment();

                bundle.putString("conteudo", "ESG");
                conteudosFragment.setArguments(bundle);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameFrag, conteudosFragment)
                        .addToBackStack("conteudos")
                        .commit();
            }
        });

        btnRios.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   ConteudosFragment conteudosFragment = new ConteudosFragment();

                   bundle.putString("conteudo", "Poluição nos Rios");
                   conteudosFragment.setArguments(bundle);

                   getParentFragmentManager().beginTransaction()
                           .replace(R.id.frameFrag, conteudosFragment)
                           .addToBackStack("conteudos")
                           .commit();
               }
           }
        );

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