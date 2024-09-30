package com.mobile.educaeco;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobile.educaeco.adapters.AdapterMissao;
import com.mobile.educaeco.adapters.AdapterVideo;
import com.mobile.educaeco.interfaces.ConteudosCallback;
import com.mobile.educaeco.interfaces.EncontrarIDAlunoCallback;
import com.mobile.educaeco.interfaces.MissoesCallback;
import com.mobile.educaeco.interfaces.VideosCallback;
import com.mobile.educaeco.models.Aluno;
import com.mobile.educaeco.models.ControleMissoes;
import com.mobile.educaeco.models.Missao;
import com.mobile.educaeco.models.Video;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

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

    public void initializeUser(String email, SharedPreferences sharedPreferences) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        // Processar o nome do usuário
        String parteLocal = email.split("@")[0];
        String[] partesNome = parteLocal.split("\\.");
        StringBuilder nomeFormatado = new StringBuilder();
        for (String parte : partesNome) {
            String capitalizada = parte.substring(0, 1).toUpperCase() + parte.substring(1).toLowerCase();
            nomeFormatado.append(capitalizada).append(" ");
        }
        String nome = nomeFormatado.toString().trim();

        AtomicLong idAluno = new AtomicLong();

        // Verificar se o aluno já existe
        db.collection("alunos")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task0 -> {
                    if (!task0.getResult().isEmpty()) {
                        Aluno aluno = task0.getResult().getDocuments().get(0).toObject(Aluno.class);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("id_aluno", task0.getResult().getDocuments().get(0).getId());
                        editor.putString("nome", aluno.getNome());
                        editor.putString("email", aluno.getEmail());
                        editor.putInt("xp", aluno.getXp());
                        editor.apply();
                    } else {
                        // Contar o número de alunos cadastrados e gerar um novo id
                        db.collection("alunos")
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        idAluno.set(task.getResult().size() + 1);
                                        user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(nome).build());

                                        // Usar o editor de SharedPreferences para salvar os dados
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("id_aluno", String.valueOf(idAluno));
                                        editor.putString("nome", nome);
                                        editor.putString("email", email);
                                        editor.putInt("xp", 0);
                                        editor.apply(); // Aplicar as mudanças de uma só vez

                                        // Salvar o novo aluno no Firestore
                                        db.collection("alunos").document(String.valueOf(idAluno))
                                                .set(new Aluno(nome, email, 0));

                                        // Inicializar as missões no Firestore
                                        AtomicLong idControleMissao = new AtomicLong();
                                        db.collection("controle_missoes")
                                                .get()
                                                .addOnCompleteListener(task2 -> {
                                                    if (task2.isSuccessful()) {
                                                        db.collection("missoes")
                                                                .get()
                                                                .addOnCompleteListener(task3 -> {
                                                                    if (task3.isSuccessful()) {
                                                                        int quantidadeMissoes = task3.getResult().size();

                                                                        for (int i = 0; i < quantidadeMissoes; i++) {
                                                                            idControleMissao.set(task2.getResult().size() + (i + 1));
                                                                            db.collection("controle_missoes").document(String.valueOf(idControleMissao))
                                                                                    .set(new ControleMissoes(false, Long.parseLong(String.valueOf(idAluno)), i + 1));
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }


    public void getMissoesByIdAlunoStatus(String idAluno, List<Missao> missoes, AdapterMissao adapterMissao, final MissoesCallback missoesCallback) {
        db.collection("controle_missoes")
                .whereEqualTo("id_aluno", Integer.parseInt(idAluno))
                .whereEqualTo("status", false)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Log.d("Missoes","não está vazio");
                        List<Integer> ids = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            ids.add(document.get("id_missao", Integer.class));
                        }
                        missoes.clear();

                        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                        for (Integer id : ids) {
                            Task<DocumentSnapshot> task = db.collection("missoes").document(id.toString()).get();
                            tasks.add(task);
                        }

                        Tasks.whenAllSuccess(tasks).addOnSuccessListener(aVoid -> {

                            for (Task<DocumentSnapshot> task : tasks) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null && document.exists()) {
                                        String descricao = document.getString("nome");
                                        String xp = document.getString("quant_xp");

                                        // Criar objeto Missão e adicionar à lista fornecida
                                        Missao missao = new Missao(descricao, Integer.parseInt(xp));
                                        missoes.add(missao);
                                    } else {
                                    }
                                } else {
                                    missoes.add(null);
                                }
                            }

                            // Notificar o adapter que os dados mudaram
                            adapterMissao.notifyDataSetChanged();
                            missoesCallback.onCallback(missoes);
                        }).addOnFailureListener(e -> {
                        });

                    }
                });
    }

    public void encontrar_id_aluno(String email, EncontrarIDAlunoCallback callback) {
        db.collection("alunos")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String id = document.getId();
                            callback.onIdFound(document.getId());
                            return;
                        }
                    } else {
                        callback.onIdFound(null);
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onIdFound(null);
                });
    }

    public void changeControleMissoes(String id_aluno, String id_missao) {
        db.collection("controle_missoes")
                .whereEqualTo("id_aluno", id_aluno)
                .whereEqualTo("id_missao", id_missao)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Boolean status = document.getBoolean("status");
                            if (status == null || !status) {
                                db.collection("controle_missoes").document(document.getId())
                                        .update("status", true);
                            }
                        }
                    }
                });
    }

    public void updateStatus(String id_aluno, String id_missao) {
        // Consulta para localizar o documento
        db.collection("controle_missoes")
                .whereEqualTo("id_aluno", Long.parseLong(id_aluno))
                .whereEqualTo("id_missao", Long.parseLong(id_missao))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            if (document.getBoolean("status") == false) {
                                db.collection("controle_missoes").document(document.getId())
                                        .update("status", true)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("Firestore", "Status atualizado com sucesso!");
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.w("Firestore", "Erro ao atualizar o status", e);
                                        });
                            }
                        }
                    } else {
                        Log.d("Firestore", "Nenhum documento encontrado para os critérios especificados.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Erro ao buscar controle de missões", e);
                });
    }


}

