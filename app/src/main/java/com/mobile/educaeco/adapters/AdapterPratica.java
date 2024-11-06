package com.mobile.educaeco.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobile.educaeco.Database;
import com.mobile.educaeco.R;
import com.mobile.educaeco.activities.Camera;
import com.mobile.educaeco.models.Pratica;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.List;

public class AdapterPratica extends RecyclerView.Adapter<AdapterPratica.ViewHolder>{
    private List<Pratica> listaPraticas;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    Database db = new Database();


    public AdapterPratica(List<Pratica> listaPraticas, ActivityResultLauncher<Intent> galleryLauncher, ActivityResultLauncher<Intent> cameraLauncher) {
        this.listaPraticas = listaPraticas;
        this.galleryLauncher = galleryLauncher;
        this.cameraLauncher = cameraLauncher;
    }
    @NonNull
    @Override
    public AdapterPratica.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pratica, parent, false);
        return new AdapterPratica.ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    public void onBindViewHolder(@NonNull AdapterPratica.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Pratica pratica = listaPraticas.get(position);

        holder.pratica.setText(pratica.getPratica());
        holder.status.setText(pratica.getStatus());
        holder.dataEntregaPratica.setText("Data de Entrega\n" + new SimpleDateFormat("dd/MM/yyyy hh:mm").format(pratica.getDataFinalizacao()));

        long currentTime = System.currentTimeMillis();

        SharedPreferences sharedPreferences = holder.itemView.getContext().getSharedPreferences("aluno", MODE_PRIVATE);
        String id_aluno = sharedPreferences.getString("id_aluno", "");

        if (pratica.getDataFinalizacao().getTime() < currentTime) {
            if( pratica.isValidacao() ) {
                if (pratica.getStatus().equals("Atividade aceita")) {
                    holder.icons.setImageResource(R.drawable.check);
                    holder.icons.setOnClickListener(null);
                } else {
                    holder.icons.setImageResource(R.drawable.wrong);
                    holder.icons.setOnClickListener(null);
                }
            } else {
                if ( pratica.getStatus().equals("Não entregue")) {
                    holder.status.setText(pratica.getStatus());
                    holder.icons.setImageResource(R.drawable.wrong);
                    holder.icons.setOnClickListener(null);
                } else if (pratica.getStatus().equals("Coloque em anexo")) {
                    pratica.setStatus("Não entregue");
                    holder.status.setText(pratica.getStatus());
                    db.updateStatusPratica(String.valueOf(pratica.getId()), id_aluno, "Não entregue");
                    holder.icons.setImageResource(R.drawable.wrong);
                    holder.icons.setOnClickListener(null);
                } else if (pratica.getStatus().equals("Ver imagem em anexo")) {
                    holder.icons.setVisibility(View.INVISIBLE);
                    holder.icons.setOnClickListener(null);
                }
            }
        } else {
            if (pratica.getStatus().equals("Ver imagem em anexo")) {
                holder.status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String imgCaminho = pratica.getImagemPratica();

                        BottomSheetDialog modalPratica = new BottomSheetDialog(v.getContext());
                        View view = LayoutInflater.from(v.getContext()).inflate(R.layout.modal_pratica_imagem, null);

                        modalPratica.setContentView(view);

                        ImageView imageView = view.findViewById(R.id.imgPratica);

                        // Carregar a imagem do Firebase Storage
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference().child(imgCaminho);

                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Usar Glide ou Picasso para carregar a imagem
                                Glide.with(v.getContext())
                                        .load(uri)
                                        .into(imageView);
                                modalPratica.show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(v.getContext(), "Erro ao carregar a imagem", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            holder.icons.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Criação Modal de Login
                    BottomSheetDialog modalPratica = new BottomSheetDialog(v.getContext());
                    View view = LayoutInflater.from(v.getContext()).inflate(R.layout.modal_pratica, null);

                    modalPratica.setContentView(view);

                    Button btnTirar = view.findViewById(R.id.btnTirarFoto);
                    btnTirar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent cameraIntent = new Intent(v.getContext(), Camera.class);
                            SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("pratica", 0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("id_pratica", String.valueOf(pratica.getId()));
                            editor.putString("pratica", pratica.getPratica());
                            editor.putString("position", String.valueOf(position));
                            editor.apply();
                            v.getContext().startActivity(cameraIntent);
                            modalPratica.dismiss();
                        }
                    });

                    // Botão para escolher da galeria
                    Button btnGaleria = view.findViewById(R.id.btnGaleria);
                    btnGaleria.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("pratica", 0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("id_pratica", String.valueOf(pratica.getId()));
                            editor.putString("pratica", pratica.getPratica());
                            editor.putString("position", String.valueOf(position));
                            editor.apply();
                            Bundle bundle = new Bundle();
                            bundle.putString("pratica", pratica.getPratica());
                            bundle.putString("status", pratica.getStatus());
                            bundle.putString("dataEntrega", new SimpleDateFormat("dd/MM/yyyy hh:mm").format(pratica.getDataFinalizacao()));
                            bundle.putString("ação", "galeria");
                            galleryIntent.putExtras(bundle);
                            galleryLauncher.launch(galleryIntent);
                            modalPratica.dismiss();
                        }
                    });

                    modalPratica.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listaPraticas.size();
    }

    public void updatePraticaStatus(int position, String newStatus) {
        if (position >= 0 && position < listaPraticas.size()) {
            listaPraticas.get(position).setStatus(newStatus); // Atualiza o status da prática
            notifyItemChanged(position); // Notifica o RecyclerView para atualizar a visualização
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView pratica;
        private TextView status;
        private TextView dataEntregaPratica;
        ImageView icons;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            pratica = itemView.findViewById(R.id.nome);
            status = itemView.findViewById(R.id.resultado);
            dataEntregaPratica = itemView.findViewById(R.id.data);
            icons = itemView.findViewById(R.id.icons);
        }
    }
}
