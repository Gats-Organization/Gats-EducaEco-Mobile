package com.mobile.educaeco.interfaces;

import com.mobile.educaeco.models.Pratica;
import com.mobile.educaeco.models.Quiz;

import java.util.List;

public interface PraticaCallback {
    void onSuccess(List<Pratica> quizzes);
    void onFailure(Exception e, List<Pratica> listaPratica);
}
