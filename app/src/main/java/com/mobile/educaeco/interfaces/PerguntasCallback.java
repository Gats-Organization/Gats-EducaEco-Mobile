package com.mobile.educaeco.interfaces;

import com.mobile.educaeco.models.Pergunta;

import java.util.List;

public interface PerguntasCallback {
    void onComplete(List<Pergunta> perguntas);
}
