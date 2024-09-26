package com.mobile.educaeco.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mobile.educaeco.R;
import com.mobile.educaeco.activities.ActivityVideo;
import com.mobile.educaeco.models.Missao;
import com.mobile.educaeco.models.Video;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdapterMissao extends RecyclerView.Adapter<AdapterMissao.ViewHolder>{

    private List<Missao> listaMissao;

    public AdapterMissao(List<Missao> listaMissao) {
        this.listaMissao = listaMissao;
    }

    @NonNull
    @Override
    public AdapterMissao.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_missao, parent, false);
        return new AdapterMissao.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterMissao.ViewHolder holder, int position) {
        Missao missao = listaMissao.get(position);

        holder.descricao.setText(missao.getDescricao());
        holder.quantXp.setText(missao.getQuantXp() + "xp");
    }

    @Override
    public int getItemCount() {
        return listaMissao.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView descricao;
        private TextView quantXp;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            descricao = itemView.findViewById(R.id.descricaoMissao);
            quantXp = itemView.findViewById(R.id.quantXp);
        }
    }
}
