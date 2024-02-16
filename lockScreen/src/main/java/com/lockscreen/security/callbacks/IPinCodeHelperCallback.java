package com.lockscreen.security.callbacks;

import com.lockscreen.security.PinCodeResult;

public interface IPinCodeHelperCallback<T> {
    void onResult(PinCodeResult<T> result);
}
