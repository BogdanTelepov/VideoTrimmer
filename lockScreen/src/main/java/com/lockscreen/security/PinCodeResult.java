package com.lockscreen.security;

public class PinCodeResult<T> {

    private SecurityError mError = null;
    private T mResult = null;

    public PinCodeResult(SecurityError mError) {
        this.mError = mError;
    }

    public PinCodeResult(T result) {
        mResult = result;
    }

    public SecurityError getError() {
        return mError;
    }

    public T getResult() {
        return mResult;
    }
}
