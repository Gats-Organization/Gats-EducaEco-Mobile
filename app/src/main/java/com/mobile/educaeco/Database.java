package com.mobile.educaeco;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobile.educaeco.adapters.AdapterVideo;
import com.mobile.educaeco.interfaces.ConteudosCallback;
import com.mobile.educaeco.interfaces.VideosCallback;
import com.mobile.educaeco.models.Video;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {

    FirebaseFirestore db;

    Map<String, Object> temas_id = new HashMap<>();

    public Database() {
        db = FirebaseFirestore.getInstance();
    }


    public void getTemasByNome(String nome, final ConteudosCallback firestoreCallback) {
        db.collection("temas")
                .whereEqualTo("nome", nome)  // Filtra pelo campo "nome"
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<String> resultList = new ArrayList<>();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String texto = document.getString("texto");

                                if (texto != null) {
                                    resultList.add(texto);
                                }
                            }
                            firestoreCallback.onCallback(resultList);
                        } else {
                            firestoreCallback.onCallback(new ArrayList<>());
                        }
                    }
                });
    }

    public void getVideosByTemaId(int temaId, List<Video> videos, AdapterVideo adapterVideo, final VideosCallback firestoreCallback) {

        db.collection("videos")
                .whereEqualTo("tema_id", temaId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            videos.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Extrair os campos do documento
                                String titulo = document.getString("titulo");
                                String videoId = document.getString("video_url");

                                // Criar objeto Video e adicionar à lista fornecida como parâmetro
                                Video video = new Video(titulo, temaId, videoId);
                                videos.add(video);

                                firestoreCallback.onCallback(videos);
                            }

                            // Notificar o adapter que os dados mudaram
                            adapterVideo.notifyDataSetChanged();

                        } else {
                            // Chamar o callback de erro caso a operação falhe
                            videos.clear();
                            firestoreCallback.onCallback(new ArrayList<>());
                            adapterVideo.notifyDataSetChanged();
                        }
                    }
                });
    }
}

