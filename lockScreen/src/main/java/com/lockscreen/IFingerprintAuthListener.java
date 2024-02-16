package com.lockscreen;

public interface IFingerprintAuthListener {
    void onAuthenticated();
    void onError();
}
