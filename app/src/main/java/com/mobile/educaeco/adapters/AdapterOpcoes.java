package com.mobile.educaeco.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.educaeco.R;
import com.mobile.educaeco.fragments.IniciarQuizFragment;
import com.mobile.educaeco.models.Opcao;
import com.mobile.educaeco.models.Quiz;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.List;

public class AdapterOpcoes extends RecyclerView.Adapter<AdapterOpcoes.ViewHolder> {

    private List<String> opcoes;
    private OnOptionSelectedListener listener;
    private int opcaoSelecionadaIndex = -1;
    private int opcaoCorretaIndex = -1;
    private boolean respostaChecada = false;

    public AdapterOpcoes(List<String> opcoes, OnOptionSelectedListener listener, int opcaoCorretaIndex) {
        this.opcoes = opcoes;
        this.opcaoCorretaIndex = opcaoCorretaIndex;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_opcao, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String opcao = opcoes.get(position);
        holder.opcao.setText(opcao);

        // Definir a cor com base no estado da resposta
        if (respostaChecada) {
            if (position == opcaoCorretaIndex) {
                holder.cardView.setBackgroundColor(Color.parseColor("#A6ECA0"));
            } else if (position == opcaoSelecionadaIndex) {
                holder.cardView.setBackgroundColor(Color.parseColor("#F59A8D"));
            } else {
                holder.cardView.setBackgroundColor(Color.parseColor("#4AAFC6"));
            }
        } else {
            // Sem verificação, mantemos a cor normal
            holder.cardView.setBackgroundColor(position == opcaoSelecionadaIndex ? Color.parseColor("#277E93") : Color.parseColor("#4AAFC6"));
        }

        holder.cardView.setOnClickListener(v -> {
            if (!respostaChecada) {
                holder.cardView.setRadius(20);
                opcaoSelecionadaIndex = position;
                listener.onOptionSelected(opcao);
                notifyDataSetChanged();
            }
        });
    }

    public void checarResposta() {
        respostaChecada = true;
        notifyDataSetChanged();
    }

    public interface OnOptionSelectedListener {
        void onOptionSelected(String opcaoSelecionada);
    }


    @Override
    public int getItemCount() {
        return opcoes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView opcao;
        public CardView cardView;
        public RelativeLayout relative;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            opcao = itemView.findViewById(R.id.opcao);
            cardView = itemView.findViewById(R.id.cardView);
            relative = itemView.findViewById(R.id.relative);
        }
    }
}

