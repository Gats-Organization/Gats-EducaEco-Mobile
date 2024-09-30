package com.mobile.educaeco.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.mobile.educaeco.Database;
import com.mobile.educaeco.R;
import com.mobile.educaeco.api.EducaEcoAPI;
import com.mobile.educaeco.interfaces.EncontrarIDAlunoCallback;
import com.mobile.educaeco.models_api.Aluno;
import com.mobile.educaeco.models_api.Escola;
import com.mobile.educaeco.models_api.Professor;
import com.mobile.educaeco.models_api.Turma;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {
    Button btnLogin;

    Database db = new Database();
    private EducaEcoAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //Para testar o login, comente essa parte
        FirebaseAuth autenticar = FirebaseAuth.getInstance();
        FirebaseUser user = autenticar.getCurrentUser();


        if (user != null) {
            Intent intent = new Intent(Login.this, Main.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Criação Modal de Login
                BottomSheetDialog modalLogin = new BottomSheetDialog(Login.this);
                View view1 = getLayoutInflater().inflate(R.layout.modal_login, null);

                modalLogin.setContentView(view1);
                modalLogin.show();

                //Criação Modal de Troca de Senha
                BottomSheetDialog modalTrocarSenha = new BottomSheetDialog(Login.this);
                View view2 = getLayoutInflater().inflate(R.layout.modal_trocar_senha, null);


                Button btnFazerLogin = view1.findViewById(R.id.btnFazerLogin);

                //Colocando a função do botão de fazer login
                btnFazerLogin.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public void onClick(View v) {
                        //Conectando os elementos do modal
                        TextInputLayout inputEmailLayout = view1.findViewById(R.id.inputEmailLayout);
                        TextInputEditText inputEmail = view1.findViewById(R.id.inputEmail);

                        TextInputLayout inputSenhaLayout = view1.findViewById(R.id.inputSenhaLayout);
                        TextInputEditText inputSenha = view1.findViewById(R.id.inputSenha);

                        inputEmailLayout.setError(null);
                        inputSenhaLayout.setError(null);

                        inputSenha.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                inputSenhaLayout.setError(null);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });

                        if (!isConnectedToInternet()) {
                            Toast.makeText(Login.this, "Sem conexão com a internet", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if ( inputEmail.getText().toString().isEmpty() || inputSenha.getText().toString().isEmpty() ) {
                            if (  inputEmail.getText().toString().isEmpty() ) {
                                inputEmailLayout.setError("Preencha o campo de e-mail!");
                            }

                            if (  inputSenha.getText().toString().isEmpty() ) {
                                inputSenhaLayout.setError("Preencha o campo de senha!");
                            }
                            Toast.makeText(Login.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                        } else {
                            autenticar.signInWithEmailAndPassword(inputEmail.getText().toString(), inputSenha.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (  task.isSuccessful() ) {
                                                //Se a senha for Gats2024@ e o email estiver correto e o botão for clicado o usuário vai trocar a senha
                                                if ( inputSenha.getText().toString().equals("Gats2024@") ) {
                                                    modalLogin.dismiss();

                                                    //Colocando o modal em tela
                                                    modalTrocarSenha.setContentView(view2);
                                                    modalTrocarSenha.show();

                                                    //Conectando os elementos do modal
                                                    TextInputEditText inputEmailTrocarSenha = view2.findViewById(R.id.inputEmail);
                                                    inputEmailTrocarSenha.setText(inputEmail.getText().toString());

                                                    TextInputEditText inputNovaSenha = view2.findViewById(R.id.inputSenha);


                                                    ImageView btnFazerLoginTrocarSenha = view2.findViewById(R.id.btnFazerLoginTrocarSenha);
                                                    //Senha redefinida
                                                    btnFazerLoginTrocarSenha.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            if (  Objects.requireNonNull(inputEmailTrocarSenha.getText()).toString().equals(inputEmail.getText().toString()) ) {
                                                                updatePassword("Gats2024@", inputNovaSenha.getText().toString());
                                                                modalTrocarSenha.dismiss();
                                                                Intent intent = new Intent(Login.this, Main.class);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                SharedPreferences sharedPreferences= getSharedPreferences("aluno", MODE_PRIVATE);
                                                                db.initializeUser(inputEmail.getText().toString(), sharedPreferences);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    modalLogin.dismiss();

                                                    Intent intent = new Intent(Login.this, Main.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    SharedPreferences sharedPreferences= getSharedPreferences("aluno", MODE_PRIVATE);
                                                    db.encontrar_id_aluno(inputEmail.getText().toString(), new EncontrarIDAlunoCallback() {
                                                        @Override
                                                        public void onIdFound(String id_aluno) {
                                                            sharedPreferences.edit().putString("id_aluno", id_aluno).apply();
                                                            String idAluno = sharedPreferences.getString("id_aluno", "");

                                                            SharedPreferences.Editor editor = sharedPreferences.edit();

                                                            pegarInformaçõesAluno(idAluno, editor, sharedPreferences, intent);
                                                        }
                                                    });
                                                }
                                            } else {
                                                String msg = "Erro ao fazer login";
                                                try {
                                                    throw task.getException();
                                                } catch (FirebaseAuthInvalidUserException e) {
                                                    msg = "O email digitado está invalido";
                                                    inputEmailLayout.setError(msg);
                                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                                    msg = "A senha e/ou email estão inválidos";
                                                    inputSenhaLayout.setError(msg);
                                                    inputEmailLayout.setError(" ");
                                                } catch (Exception e) {
                                                    msg = e.getMessage();
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
        });
    }

    public void updatePassword(String senha, String novaSenha) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        System.out.println(senha);
        System.out.println(novaSenha);

        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), senha);

            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.updatePassword(novaSenha);
                        } else {
                            System.out.println("Falha na reautenticação: " + task.getException().getMessage());
                        }
                    });
        }
    }

    public boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public void pegarInformaçõesAluno(String idAluno, SharedPreferences.Editor editor, SharedPreferences sharedPreferences, Intent intent) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gats-repository-api.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(EducaEcoAPI.class);

        Call<Aluno> call = api.getAluno(idAluno);
        call.enqueue(new Callback<Aluno>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Aluno> call, Response<Aluno> response) {
                Aluno aluno = response.body();

                if (aluno != null) {
                    editor.putString("nome", aluno.getNome() + " " + aluno.getSobrenome());
                    editor.putString("email", aluno.getEmail());

                    Turma turma = aluno.getTurma();
                    editor.putString("turma", turma.getSerie() + " ano " + turma.getNomenclatura());
                    Escola escola = turma.getEscola();
                    editor.putString("escola", escola.getNome());
                    Professor professor = turma.getProfessor();
                    editor.putString("professor", professor.getNome());
                    editor.putInt("xp", aluno.getXp());
                    editor.commit();

                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Login.this, "Falha ao carregar os dados do usuário!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Aluno> call, Throwable t) {
                Toast.makeText(Login.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
