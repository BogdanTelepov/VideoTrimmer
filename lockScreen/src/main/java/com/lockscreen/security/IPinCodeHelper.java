package com.lockscreen.security;

import android.content.Context;

import com.lockscreen.security.callbacks.IPinCodeHelperCallback;

public interface IPinCodeHelper {

    /**
     * Encode pin
     *
     * @param context  any context.
     * @param pin      pin code string to check.
     * @param callback PFPinCodeHelperCallback callback object.
     * @return true if pin codes matches.
     * @throws SecurityException throw exception if something went wrong.
     */
    void encodePin(Context context, String pin, IPinCodeHelperCallback<String> callBack);

    /**
     * Check if pin code is valid.
     *
     * @param context    any context.
     * @param encodedPin encoded pin code string.
     * @param pin        pin code string to check.
     * @param callback   PFPinCodeHelperCallback callback object.
     * @return true if pin codes matches.
     * @throws SecurityException throw exception if something went wrong.
     */
    void checkPin(Context context, String encodedPin, String pin, IPinCodeHelperCallback<Boolean> callback);

    /**
     * Delete pin code encryption key.
     *
     * @param callback PFPinCodeHelperCallback callback object.
     * @throws SecurityException throw exception if something went wrong.
     */
    void delete(IPinCodeHelperCallback<Boolean> callback);

    /**
     * Check if pin code encryption key is exist.
     *
     * @param callback PFPinCodeHelperCallback callback object.
     * @return true if key exist in KeyStore.
     * @throws SecurityException throw exception if something went wrong.
     */
    void isPinCodeEncryptionKeyExist(IPinCodeHelperCallback<Boolean> callback);

}
