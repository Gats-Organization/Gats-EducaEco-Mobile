package com.mobile.educaeco.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.educaeco.NetworkUtil;
import com.mobile.educaeco.R;
import com.mobile.educaeco.activities.ActivityVideo;
import com.mobile.educaeco.activities.AdminResultados;
import com.mobile.educaeco.models.Video;
import com.mobile.educaeco.models_api.Resultado;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AdapterResultados extends RecyclerView.Adapter<AdapterResultados.ViewHolder> {
    public List<Resultado> listaResultados = new ArrayList<>();

    public void setResultados(List<Resultado> resultados) {
        this.listaResultados = resultados;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdapterResultados.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resultado, parent, false);
        return new AdapterResultados.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterResultados.ViewHolder holder, int position) {
        Resultado resultado = listaResultados.get(position);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        if (resultado != null) {
            holder.nome.setText(resultado.getNome());
            holder.email.setText(resultado.getEmail());
            holder.resultado.setText(resultado.getResultado());
            holder.data.setText(resultado.getDataRegistro()
                    .atZone(ZoneId.of("America/Sao_Paulo")).format(formatter));

            if (  holder.resultado.getText().toString().equals("Estudantil")) {
                holder.icone.setImageResource(R.drawable.icone_estudantil);
                holder.nome.setTextColor(Color.parseColor("#FFFFFF"));
                holder.email.setTextColor(Color.parseColor("#FFFFFF"));
                holder.resultado.setTextColor(Color.parseColor("#FFFFFF"));
                holder.data.setTextColor(Color.parseColor("#FFFFFF"));
                holder.cardItem.setCardBackgroundColor(Color.parseColor("#61CEFF"));
            } else {
                holder.icone.setImageResource(R.drawable.icone_sem_motivacao);
                holder.nome.setTextColor(Color.parseColor("#6D6D6D"));
                holder.email.setTextColor(Color.parseColor("#6D6D6D"));
                holder.resultado.setTextColor(Color.parseColor("#6D6D6D"));
                holder.data.setTextColor(Color.parseColor("#6D6D6D"));
                holder.cardItem.setCardBackgroundColor(Color.parseColor("#FFE79C"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return listaResultados.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nome;
        private TextView email;
        private TextView resultado;
        private TextView data;
        private ImageView icone;
        private CardView cardItem;


        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.nome);
            email = itemView.findViewById(R.id.email);
            resultado = itemView.findViewById(R.id.resultado);
            data = itemView.findViewById(R.id.data);
            icone = itemView.findViewById(R.id.icone);
            cardItem = itemView.findViewById(R.id.cardItem);
        }
    }
}
