package com.mobile.educaeco;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class Login extends AppCompatActivity {
    ImageView btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

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
                BottomSheetDialog modalConfirmarEsqueceuSenha = new BottomSheetDialog(Login.this);
                View view3 = getLayoutInflater().inflate(R.layout.modal_confirmar_esqueceu_senha, null);

                TextView EsqueceuSenhaLogin = view1.findViewById(R.id.EsqueceuASenhaLogin);

                ImageView btnFazerLogin = view1.findViewById(R.id.btnFazerLoginTrocarSenha);

                //Colocando a função do botão de esqueceu a senha
                EsqueceuSenhaLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        modalLogin.dismiss();
                        modalConfirmarEsqueceuSenha.setContentView(view3);
                        modalConfirmarEsqueceuSenha.show();

                        ImageView btnNao = view3.findViewById(R.id.btnNão);
                        ImageView btnSim = view3.findViewById(R.id.btnSim);

                        btnNao.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                modalConfirmarEsqueceuSenha.dismiss();
                            }
                        });
                        btnSim.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(Login.this, "Próximo Modal", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                //Colocando a função do botão de fazer login
                btnFazerLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Colocando o modal em tela


                        //Conectando os elementos do modal
                        TextInputLayout inputEmailLayout = view1.findViewById(R.id.inputEmailLayout);
                        TextInputEditText inputEmail = view1.findViewById(R.id.inputEmailTrocarSenha);

                        TextInputLayout inputSenhaLayout = view1.findViewById(R.id.inputSenhaLayout);
                        TextInputEditText inputSenha = view1.findViewById(R.id.inputNovaSenha);


                        //Requisito de terminar o email com @gats.com
                        if( Objects.requireNonNull(inputEmail.getText()).toString().isEmpty() && !inputEmail.getText().toString().endsWith("@gats.com")) {
                            inputEmailLayout.setError("Preencha o campo de e-mail corretamente!");
                        }
                        //Requisito de ter uma senha que não seja vazia
                        if( Objects.requireNonNull(inputSenha.getText()).toString().isEmpty()) {
                            inputSenhaLayout.setError("Preencha o campo de senha corretamente!");
                        }
                        //Se a senha for Gats2024@ e o email estiver correto e o botão for clicado o usuário vai trocar a senha
                        if ( inputSenha.getText().toString().equals("Gats2024@") ) {
                            modalLogin.dismiss();

                            //Colocando o modal em tela
                            modalTrocarSenha.setContentView(view2);
                            modalTrocarSenha.show();

                            //Conectando os elementos do modal
                            TextInputEditText inputEmailTrocarSenha = view2.findViewById(R.id.inputEmailTrocarSenha);
                            inputEmailTrocarSenha.setText(inputEmail.getText().toString());

                            ImageView btnFazerLoginTrocarSenha = view2.findViewById(R.id.btnFazerLoginTrocarSenha);
                            //Senha redefinida
                            btnFazerLoginTrocarSenha.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (  Objects.requireNonNull(inputEmailTrocarSenha.getText()).toString().equals(inputEmail.getText().toString()) ) {
                                        modalTrocarSenha.dismiss();
                                        Intent intent = new Intent(Login.this, Home.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }
                        else if (  Objects.requireNonNull(inputEmail.getText()).toString().isEmpty() == false
                                && Objects.requireNonNull(inputSenha.getText()).toString().isEmpty() == false
                        ) {
                            modalLogin.dismiss();

                            Intent intent = new Intent(Login.this, Home.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        });


    }
}
