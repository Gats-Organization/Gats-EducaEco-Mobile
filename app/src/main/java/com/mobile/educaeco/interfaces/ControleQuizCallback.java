package com.mobile.educaeco.interfaces;

import com.mobile.educaeco.models.ControleQuiz;

public interface ControleQuizCallback {
    void onCompleted(ControleQuiz controleQuiz);
    void onIncomplete();
    void onFailure(Exception e);
}

