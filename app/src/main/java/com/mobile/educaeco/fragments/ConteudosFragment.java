package com.mobile.educaeco.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobile.educaeco.Database;
import com.mobile.educaeco.R;
import com.mobile.educaeco.interfaces.ConteudosCallback;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConteudosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConteudosFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Database db = new Database();
    VideosFragment videosFragment;

    public ConteudosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConteudosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConteudosFragment newInstance(String param1, String param2) {
        ConteudosFragment fragment = new ConteudosFragment();
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
        return inflater.inflate(R.layout.fragment_conteudos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        System.out.println(bundle);
        assert bundle != null;
        String conteudo = bundle.getString("conteudo");

        TextView btnVideo = view.findViewById(R.id.btnVideos);
        ImageView btnVoltar = view.findViewById(R.id.voltar);
        ImageView erroConteudos = view.findViewById(R.id.erroConteudos);

        erroConteudos.setVisibility(View.INVISIBLE);

        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videosFragment = new VideosFragment();

                int tema_id = 0;

                if ( conteudo.equals("5 R's") ) {
                    tema_id = 1;
                } else if( conteudo.equals("Cidades Inteligentes") ) {
                    tema_id = 3;
                } else if (  conteudo.equals("ESG") ) {
                    tema_id = 2;
                } else if ( conteudo.equals("Poluição nos Rios") ) {
                    tema_id = 4;
                }

                bundle.putInt("tema_id", tema_id);
                videosFragment.setArguments(bundle);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameFrag, videosFragment)
                        .addToBackStack("videos")
                        .commit();
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                }
            }
        });


        TextView texto = view.findViewById(R.id.textoConteudo);
        ProgressBar loading = view.findViewById(R.id.loading);

        loading.setVisibility(View.VISIBLE);


        db.getTemasByNome(conteudo, new ConteudosCallback() {
            @Override
            public void onCallback(List<String> resultList) {
                loading.setVisibility(View.GONE);

                if ( resultList != null && resultList.size() >= 1 ) {
                    String textoBanco = resultList.get(0);

                    if ( texto != null ) {
                        texto.setText(HtmlCompat.fromHtml(textoBanco, HtmlCompat.FROM_HTML_MODE_LEGACY));
                    }
                } else {
                    erroConteudos.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}