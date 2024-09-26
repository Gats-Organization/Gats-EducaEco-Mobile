package com.mobile.educaeco.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobile.educaeco.Database;
import com.mobile.educaeco.R;
import com.mobile.educaeco.adapters.AdapterVideo;
import com.mobile.educaeco.interfaces.VideosCallback;
import com.mobile.educaeco.models.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideosFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Database db = new Database();
    RecyclerView recyclerVideos;
    AdapterVideo adapterVideos;
    List<Video> listaVideos = new ArrayList<>();
    ConteudosFragment conteudosFragment;

    public VideosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VideosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideosFragment newInstance(String param1, String param2) {
        VideosFragment fragment = new VideosFragment();
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
        return inflater.inflate(R.layout.fragment_videos, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView btnConteudos = view.findViewById(R.id.btnConteudos);
        ImageView btnVoltar = view.findViewById(R.id.voltar);
        ProgressBar loading = view.findViewById(R.id.loading);

        loading.setVisibility(View.VISIBLE);

        recyclerVideos = view.findViewById(R.id.listaVideos);

        adapterVideos = new AdapterVideo(listaVideos);
        recyclerVideos.setLayoutManager(new LinearLayoutManager(getContext()));

        db = new Database();

        Bundle bundle = getArguments();
        int tema_id;
        if ( bundle != null ) {
            tema_id = Integer.parseInt(String.valueOf(bundle.getInt("tema_id")));
            db.getVideosByTemaId(tema_id, listaVideos, adapterVideos, new VideosCallback() {
                @Override
                public void onCallback(List<Video> videoList) {
                    loading.setVisibility(View.GONE);

                    adapterVideos = new AdapterVideo(videoList);
                    recyclerVideos.setAdapter(adapterVideos);
                }
            });

        }

        btnConteudos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conteudosFragment = new ConteudosFragment();

                conteudosFragment.setArguments(bundle);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameFrag, conteudosFragment)
                        .addToBackStack("conteudos")
                        .commit();
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager() != null && getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                }
            }
        });

        recyclerVideos.setAdapter(adapterVideos);
    }
}