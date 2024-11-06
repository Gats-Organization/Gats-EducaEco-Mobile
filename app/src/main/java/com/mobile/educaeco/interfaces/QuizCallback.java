package com.mobile.educaeco.interfaces;

import com.google.android.gms.tasks.OnFailureListener;
import com.mobile.educaeco.models.Quiz;

import java.util.List;

public interface QuizCallback {
    void onSuccess(List<Quiz> quizzes);
    void onFailure(Exception e, List<Quiz> quizzes);
}

