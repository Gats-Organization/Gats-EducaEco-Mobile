package com.mobile.educaeco;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.mobile.educaeco.adapters.AdapterMissao;
import com.mobile.educaeco.adapters.AdapterPratica;
import com.mobile.educaeco.adapters.AdapterQuiz;
import com.mobile.educaeco.adapters.AdapterVideo;
import com.mobile.educaeco.api.EducaEcoAPI;
import com.mobile.educaeco.interfaces.ConteudosCallback;
import com.mobile.educaeco.interfaces.ControleQuizCallback;
import com.mobile.educaeco.interfaces.EncontrarIDAlunoCallback;
import com.mobile.educaeco.interfaces.MissoesCallback;
import com.mobile.educaeco.interfaces.PerguntasCallback;
import com.mobile.educaeco.interfaces.PraticaCallback;
import com.mobile.educaeco.interfaces.QuizCallback;
import com.mobile.educaeco.interfaces.QuizDBCallback;
import com.mobile.educaeco.interfaces.VideosCallback;
import com.mobile.educaeco.interfaces.XPCallback;
import com.mobile.educaeco.models.Aluno;
import com.mobile.educaeco.models.ControleMissoes;
import com.mobile.educaeco.models.ControlePratica;
import com.mobile.educaeco.models.ControleQuiz;
import com.mobile.educaeco.models.Missao;
import com.mobile.educaeco.models.Pergunta;
import com.mobile.educaeco.models.Pratica;
import com.mobile.educaeco.models.Quiz;
import com.mobile.educaeco.models.QuizDB;
import com.mobile.educaeco.models.Video;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

    public void updateStatus(Long id_aluno, Long id_missao) {
        // Consulta para localizar o documento
        db.collection("controle_missoes")
                .whereEqualTo("id_aluno", id_aluno)
                .whereEqualTo("id_missao",id_missao)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.getResult().isEmpty()) {
                        db.collection("controle_missoes")
                                .document(task.getResult().getDocuments().get(0).getId())
                                .update("status", true);
                    }
                });
    }

    public void getXPbyMissao(int id_missao, XPCallback xpCallback) {
        db.collection("missoes")
                .document(String.valueOf(id_missao))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            String xp = task.getResult().getString("quant_xp");
                            int xpInt = Integer.parseInt(xp);
                            xpCallback.onCallback(xpInt);
                        } else {
                            xpCallback.onCallback(0);
                        }
                    } else {
                        xpCallback.onCallback(0);
                    }
                });
    }

    public void updateXp(String email, int somaXp) {
        db.collection("alunos")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.getResult().isEmpty()) {
                        db.collection("alunos")
                                .document(task.getResult().getDocuments().get(0).getId())
                                .update("xp", task.getResult().getDocuments().get(0).getLong("xp") + somaXp);
                    }
                });
    }

    public void getQuizPerguntasById(String id_quiz, Consumer<List<String>> callback) {
        db.collection("quiz").document(id_quiz)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.exists()) {
                        List<String> perguntasIds = (List<String>) queryDocumentSnapshots.get("perguntasIds");
                        callback.accept(perguntasIds != null ? perguntasIds : new ArrayList<>());
                    } else {
                        callback.accept(new ArrayList<>());
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    callback.accept(new ArrayList<>());
                });
    }


    public void getQuizTema(String id_quiz, Consumer<String> callback) {
        Log.d("Database", "getQuizTema: " + id_quiz);
        db.collection("quiz").document(id_quiz)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.exists()) {
                        callback.accept(queryDocumentSnapshots.getString("tema"));
                        Log.d("Database", "Tema: " + queryDocumentSnapshots.getString("tema"));
                    } else {
                        callback.accept(null);
                        Log.e("Database", "Quiz não encontrado");
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Log.e("Database", "Erro ao buscar quiz: " + e.getMessage());
                    callback.accept(null);
                });
    }

    public void getPraticaTitle(int id_pratica, Consumer<String> callback) {
        db.collection("praticas").document(String.valueOf(id_pratica))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.exists()) {
                        callback.accept(queryDocumentSnapshots.getString("pratica"));
                    } else {
                        callback.accept(null);
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    callback.accept(null);
                });
    }


    public void getQuizzes(String id_aluno, List<Quiz> quizzes, AdapterQuiz adapterQuiz, final QuizCallback quizCallback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        final Long[] id_turma = {0L};

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gats-educaeco-api-dev2-pe6e.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EducaEcoAPI api = retrofit.create(EducaEcoAPI.class);

        Log.d("Database", "getQuizzes: " + id_aluno);

        Call<com.mobile.educaeco.models_api.Aluno> call = api.getAluno(id_aluno);
        call.enqueue(new Callback<com.mobile.educaeco.models_api.Aluno>() {
            @Override
            public void onResponse(Call<com.mobile.educaeco.models_api.Aluno> call, Response<com.mobile.educaeco.models_api.Aluno> response) {
                if (response.isSuccessful()) {
                    com.mobile.educaeco.models_api.Aluno aluno = response.body();
                    if (aluno != null) {
                        id_turma[0] = aluno.getTurma().getId();

                        firestore.collection("atribuir_quiz")
                                .whereArrayContains("id_turmas", Integer.parseInt(id_turma[0].toString()))
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    quizzes.clear();

                                    // Iniciar o processamento dos quizzes
                                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                        String id_quiz = document.get("id_quiz", String.class);
                                        Timestamp dataEntrega = document.getTimestamp("dataEntrega");
                                        Timestamp dataCriacao = document.getTimestamp("dataInicio");

                                        // Usar o método assíncrono para obter o tema
                                        getQuizTema(id_quiz, tema -> {
                                            // Usar o método assíncrono para obter as perguntas
                                            getQuizPerguntasById(id_quiz, perguntasIds -> {
                                                Log.d("Perguntas:", perguntasIds.toString());
                                                // Verifica se a data de entrega ainda não passou
                                                if (dataEntrega != null
                                                        && new Date().before(dataEntrega.toDate())
                                                        && dataCriacao != null
                                                        && new Date().after(dataCriacao.toDate())) {
                                                    Log.d("Data de entrega: IF", dataEntrega.toDate().toString());
                                                    Log.d("Data Criação:", dataCriacao.toDate().toString());
                                                    Log.d("Perguntas:", perguntasIds.toString());

                                                    Quiz quiz = new Quiz(id_quiz, tema, dataCriacao.toDate(), dataEntrega.toDate(), perguntasIds);
                                                    quizzes.add(quiz);

                                                    // Atualiza o adapter e chama o callback com a lista completa
                                                    adapterQuiz.notifyDataSetChanged();
                                                    quizCallback.onSuccess(quizzes);
                                                } else {
                                                    Log.d("Data de entrega: ELSE", dataEntrega.toDate().toString());
                                                }
                                            });
                                        });
                                    }
                                }).addOnFailureListener(e -> {
                                    e.printStackTrace();
                                    quizCallback.onFailure(e, quizzes);
                                });
                    } else {
                        quizCallback.onFailure(new Exception("Erro ao buscar o aluno"), quizzes);
                        Log.e("Database", "Erro ao buscar o aluno: " + response.code());
                    }
                } else {
                    quizCallback.onFailure(new Exception("Erro ao buscar o aluno"), quizzes);
                    Log.e("Database", "Erro ao buscar o aluno: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<com.mobile.educaeco.models_api.Aluno> call, Throwable t) {
                quizCallback.onFailure((Exception) t, quizzes);
            }
        });
    }

    public void getQuizIdsPerguntasByTema(String tema, QuizDBCallback callback) {
        db.collection("quiz")
                .whereEqualTo("tema", tema)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<String> perguntasIds = new ArrayList<>();

                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            List<String> ids = (List<String>) document.get("perguntasIds");
                            if (ids != null) {
                                perguntasIds.addAll(ids);
                            }
                        }

                        String temaBD = queryDocumentSnapshots.getDocuments().get(0).getString("tema");
                        callback.onComplete(new QuizDB(temaBD, perguntasIds));
                    } else {
                        callback.onComplete(new QuizDB(null, null));
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    callback.onComplete(new QuizDB(null, null));
                });
    }

    public void getPerguntas(List<String> perguntasIds, PerguntasCallback callback) {
        List<Pergunta> perguntas = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(perguntasIds.size()); // Para contar as consultas concluídas

        for (String id : perguntasIds) {
            db.collection("perguntas")
                    .document(String.valueOf(id))
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot != null) {
                            Pergunta pergunta = documentSnapshot.toObject(Pergunta.class);
                            if (pergunta != null) {
                                perguntas.add(pergunta);
                            }
                        }
                        latch.countDown(); // Decrementa o contador ao finalizar a consulta
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        latch.countDown(); // Também decrementa em caso de falha
                    });
        }

        // Usar um novo thread para aguardar as consultas
        new Thread(() -> {
            try {
                latch.await(); // Espera até que todas as consultas sejam concluídas
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            callback.onComplete(perguntas); // Chama o callback após todas as consultas
        }).start();
    }

    public void verificarRegistroControleQuiz(String id_aluno, String idQuiz, ControleQuizCallback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("controle_quiz")
                .whereEqualTo("id_aluno", Integer.parseInt(id_aluno))
                .whereEqualTo("id_quiz", idQuiz)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        ControleQuiz controleQuiz = queryDocumentSnapshots.getDocuments().get(0).toObject(ControleQuiz.class);
                        if (controleQuiz != null) {
                            Log.d("ControleQuiz", "Quiz encontrado");
                            callback.onCompleted(controleQuiz); // Quiz completo, exibir resultados
                        } else {
                            Log.d("ControleQuiz", "Quiz incompleto");
                            callback.onIncomplete(); // Quiz incompleto, iniciar normalmente
                        }
                    } else {
                        Log.d("ControleQuiz", "Quiz não encontrado");
                        callback.onIncomplete(); // Não há registro, iniciar o quiz
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getPraticas(String id_aluno, List<Pratica> listaPraticas, AdapterPratica adapterPratica, final PraticaCallback praticaCallback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        final Long[] id_turma = {0L};

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gats-educaeco-api-dev2-pe6e.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EducaEcoAPI api = retrofit.create(EducaEcoAPI.class);

        Call<com.mobile.educaeco.models_api.Aluno> call = api.getAluno(id_aluno);
        call.enqueue(new Callback<com.mobile.educaeco.models_api.Aluno>() {
            @Override
            public void onResponse(Call<com.mobile.educaeco.models_api.Aluno> call, Response<com.mobile.educaeco.models_api.Aluno> response) {
                if (response.isSuccessful()) {
                    com.mobile.educaeco.models_api.Aluno aluno = response.body();
                    if (aluno != null) {
                        id_turma[0] = aluno.getTurma().getId();

                        firestore.collection("atribuir_pratica")
                                .whereArrayContains("id_turmas", Integer.parseInt(id_turma[0].toString()))
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    listaPraticas.clear();

                                    // Iniciar o processamento das práticas
                                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                        Integer id_pratica = document.get("id_pratica", Integer.class);
                                        Timestamp dataEntregaFim = document.getTimestamp("dataEntrega");
                                        Timestamp dataCriacao = document.getTimestamp("dataInicio");
                                        final Date[] dataEntrega = new Date[1];
                                        final String[] imgPratica = new String[1];
                                        Log.d("Pratica", String.valueOf(id_pratica));

                                        db.collection("controle_pratica")
                                                .document(String.valueOf(id_pratica) + "_" + id_aluno)
                                                .get()
                                                .addOnSuccessListener(documentSnapshot -> {
                                                    String status = documentSnapshot.getString("status");
                                                    dataEntrega[0] = documentSnapshot.getDate("dataEntrega") != null ? documentSnapshot.getDate("dataEntrega") : null;
                                                    imgPratica[0] = documentSnapshot.getString("imgCaminhoStorage") != null ? documentSnapshot.getString("imgCaminhoStorage") : "";
                                                    boolean validacao = documentSnapshot.getBoolean("validacao") != null && Boolean.TRUE.equals(documentSnapshot.getBoolean("validacao"));
                                                    getPraticaTitle(id_pratica, tema -> {
                                                        Pratica pratica = new Pratica(id_pratica, tema, status, dataCriacao.toDate(), dataEntregaFim.toDate(), dataEntrega[0], imgPratica[0], validacao);
                                                        listaPraticas.add(pratica);
                                                        adapterPratica.notifyDataSetChanged();
                                                        praticaCallback.onSuccess(listaPraticas);
                                                    });
                                                })
                                                .addOnFailureListener(e -> {
                                                    e.printStackTrace();
                                                    praticaCallback.onFailure(e, listaPraticas);
                                                });
                                    }
                                }).addOnFailureListener(e -> {
                                    e.printStackTrace();
                                    praticaCallback.onFailure(e, listaPraticas);
                                });
                    } else {
                        praticaCallback.onFailure(new Exception("Aluno não encontrado"), listaPraticas);
                    }
                }
            }

            @Override
            public void onFailure(Call<com.mobile.educaeco.models_api.Aluno> call, Throwable t) {
                t.printStackTrace();
                praticaCallback.onFailure((Exception) t, listaPraticas);
            }
        });
    }

    public void uploadFoto(Context c, ImageView foto, Map<String, String> docData, String id_pratica) {
        //conversão
        Bitmap bitmap = ((BitmapDrawable)foto.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] databyte = baos.toByteArray();


        SharedPreferences sharedPreferences2 = c.getSharedPreferences("aluno", MODE_PRIVATE);
        String nome = sharedPreferences2.getString("nome", "");
        String turma = sharedPreferences2.getString("turma", "");

        FirebaseStorage storage = FirebaseStorage.getInstance();
        String caminho = "pratica-" + id_pratica + "-" + nome + "-" + turma + ".jpg";
        SharedPreferences sharedPreferences = c.getSharedPreferences("pratica", MODE_PRIVATE);
        sharedPreferences.edit().putString("imageUri", caminho).apply();
        storage.getReference("galeria").child(caminho)
                .putBytes(databyte)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //obter a URL da imagem
                        taskSnapshot.getMetadata().getReference().getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        docData.put("url", uri.toString());
                                    }
                                });
                    }
                });
    }

    public void downloadFoto(ImageView img, Uri urlFirebase){
        img.setRotation(0);
        Glide.with(img.getContext()).asBitmap().load(urlFirebase).into(img);
    }

    public void registroPratica(String pratica, String id_aluno, String imgCaminhoStorage, Date dataEntrega) {
        // Criação do mapa com os dados para registro
        Log.d("DEU GREEN", pratica + "_" + id_aluno);

        Map<String, Object> praticaData = new HashMap<>();
        praticaData.put("status", "Ver imagem em anexo");  // Atualiza o status
        praticaData.put("dataEntrega", dataEntrega);       // Data de entrega
        praticaData.put("imgCaminhoStorage", imgCaminhoStorage); // Caminho da imagem no Storage
        praticaData.put("validacao", false);               // Validação inicial, ainda não avaliado pelo professor

        // Cria/Atualiza o documento no Firestore
        db.collection("controle_pratica").document(pratica + "_" + id_aluno)
                .update(praticaData)  // Usar update para manter campos antigos (ex: id_pratica, id_aluno)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("DEU GREEN", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ERROR", "Error updating document", e);
                    }
                });
    }



    public void validarPratica(String praticaId, String id_aluno, String statusValidacao) {
        db.collection("controle_pratica").document(praticaId + "_" + id_aluno)
                .update("statusValidacao", statusValidacao)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Validação", "Prática validada com sucesso!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Validação", "Erro ao validar prática", e);
                    }
                });
    }

    public void updateStatusPratica(String praticaId, String id_aluno, String status) {
        db.collection("controle_pratica").document(praticaId + "_" + id_aluno)
                .update("status", status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("STATUS", "Prática atualizada com sucesso!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("STATUS", "Erro ao atualizar prática", e);
                    }
                });
    }
}

