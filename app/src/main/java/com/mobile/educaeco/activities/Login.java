package com.mobile.educaeco.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.auth.SignInMethodQueryResult;
import com.mobile.educaeco.R;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class Login extends AppCompatActivity {
    Button btnLogin;
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

                //Criação Modal de Confirmar Esqueceu a Senha
                BottomSheetDialog modalEmailEsqueceuSenha = new BottomSheetDialog(Login.this);
                View view3 = getLayoutInflater().inflate(R.layout.modal_confirmar_esqueceu_senha, null);

                //Criação Modal de Esqueceu a Senha
                BottomSheetDialog modalEsqueceuSenha = new BottomSheetDialog(Login.this);
                View view4 = getLayoutInflater().inflate(R.layout.modal_esqueceu_senha, null);


                TextView EsqueceuSenhaLogin = view1.findViewById(R.id.EsqueceuASenhaLogin);

                Button btnFazerLogin = view1.findViewById(R.id.btnFazerLogin);

                //Colocando a função do botão de esqueceu a senha
                EsqueceuSenhaLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        modalLogin.dismiss();
                        modalEmailEsqueceuSenha.setContentView(view3);
                        modalEmailEsqueceuSenha.show();

                        ImageView btnRedefinir = view3.findViewById(R.id.btnRedefinir);

                        btnRedefinir.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                modalEmailEsqueceuSenha.dismiss();
                                modalEsqueceuSenha.setContentView(view4);
                                modalEsqueceuSenha.show();
                            }
                        });

                    }
                });

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
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    modalLogin.dismiss();

                                                    Intent intent = new Intent(Login.this, Main.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
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
}
