package com.lockscreen.security;

public class SecurityManager {
    private static final SecurityManager ourInstance = new SecurityManager();

    public static SecurityManager getInstance() {
        return ourInstance;
    }

    private SecurityManager() {
    }

    private IPinCodeHelper mPinCodeHelper = FingerprintPinCodeHelper.getInstance();

    public void setPinCodeHelper(IPinCodeHelper pinCodeHelper) {
        mPinCodeHelper = pinCodeHelper;
    }

    public IPinCodeHelper getPinCodeHelper() {
        return mPinCodeHelper;
    }
}
