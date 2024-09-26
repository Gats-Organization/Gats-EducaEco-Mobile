package com.mobile.educaeco.interfaces;

public interface InitializeUserCallback {
    void onUserInitialized(int userId);
    void onError(Exception e);
}