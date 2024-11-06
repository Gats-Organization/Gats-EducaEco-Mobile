package com.mobile.educaeco.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.educaeco.NetworkUtil;
import com.mobile.educaeco.R;
import com.mobile.educaeco.activities.ActivityVideo;
import com.mobile.educaeco.fragments.RankingFragment;
import com.mobile.educaeco.models.Video;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.List;

public class AdapterVideo extends RecyclerView.Adapter<AdapterVideo.ViewHolder>{

    public List<Video> listaVideo;
    private Context context;

    public AdapterVideo(List<Video> listaVideo, Context context) {
        this.listaVideo = listaVideo;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterVideo.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterVideo.ViewHolder holder, int position) {
        Video video = listaVideo.get(position);

        if (video != null) {
            holder.tituloVideo.setText(video.getTitulo());

            holder.video.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    youTubePlayer.loadVideo(video.getVideo_url(), 0);
                }
            });

            holder.btnExpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if ( NetworkUtil.isNetworkAvailable(context) ) {
                        Intent intent = new Intent(holder.itemView.getContext(), ActivityVideo.class);
                        intent.putExtra("video_url", video.getVideo_url());
                        holder.itemView.getContext().startActivity(intent);
                    } else {
                        showNoInternetToast();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listaVideo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tituloVideo;
        private YouTubePlayerView video;
        private ImageView btnExpand;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tituloVideo = itemView.findViewById(R.id.tituloVideo);
            video = itemView.findViewById(R.id.video);
            btnExpand = itemView.findViewById(R.id.btnExpand);
        }
    }

    private void showNoInternetToast() {
        Toast.makeText(context, "Sem conexão com a internet. Verifique e tente novamente.", Toast.LENGTH_LONG).show();
    }
}

