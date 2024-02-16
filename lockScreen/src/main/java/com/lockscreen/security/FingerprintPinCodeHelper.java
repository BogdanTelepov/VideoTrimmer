package com.lockscreen.security;

import android.content.Context;

import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import com.lockscreen.security.callbacks.IPinCodeHelperCallback;

/**
 * PFFingerprintPinCodeHelper - helper class to encode/decode pin code string,
 * validate pin code etc.
 */
public class FingerprintPinCodeHelper implements IPinCodeHelper {


    private static final String FINGERPRINT_ALIAS = "fp_fingerprint_lock_screen_key_store";
    private static final String PIN_ALIAS = "fp_pin_lock_screen_key_store";

    private static final FingerprintPinCodeHelper ourInstance = new FingerprintPinCodeHelper();

    public static FingerprintPinCodeHelper getInstance() {
        return ourInstance;
    }

    private final ISecurityUtils pfSecurityUtils
            = SecurityUtilsFactory.getPFSecurityUtilsInstance();

    private FingerprintPinCodeHelper() {

    }

    /**
     * Encode pin code.
     *
     * @param context any context.
     * @param pin     pin code string.
     * @return encoded pin code string.
     * @throws SecurityException throw exception if something went wrong.
     */
    @Override
    public void encodePin(Context context, String pin, IPinCodeHelperCallback<String> callback) {
        try {
            final String encoded = pfSecurityUtils.encode(context, PIN_ALIAS, pin, false);
            if (callback != null) {
                callback.onResult(new PinCodeResult(encoded));
            }
        } catch (SecurityException e) {
            if (callback != null) {
                callback.onResult(new PinCodeResult(e.getError()));
            }
        }
    }

    /**
     * Check if pin code is valid.
     *
     * @param context    any context.
     * @param encodedPin encoded pin code string.
     * @param pin        pin code string to check.
     * @return true if pin codes matches.
     * @throws SecurityException throw exception if something went wrong.
     */
    @Override
    public void checkPin(Context context, String encodedPin, String pin, IPinCodeHelperCallback<Boolean> callback) {
        try {
            final String pinCode = pfSecurityUtils.decode(PIN_ALIAS, encodedPin);
            if (callback != null) {
                callback.onResult(new PinCodeResult(pinCode.equals(pin)));
            }
        } catch (SecurityException e) {
            if (callback != null) {
                callback.onResult(new PinCodeResult(e.getError()));
            }
        }
    }


    private boolean isFingerPrintAvailable(Context context) {
        return FingerprintManagerCompat.from(context).isHardwareDetected();
    }

    private boolean isFingerPrintReady(Context context) {
        return FingerprintManagerCompat.from(context).hasEnrolledFingerprints();
    }

    /**
     * Delete pin code encryption key.
     *
     * @throws SecurityException throw exception if something went wrong.
     */
    @Override
    public void delete(IPinCodeHelperCallback<Boolean> callback) {
        try {
            pfSecurityUtils.deleteKey(PIN_ALIAS);
            if (callback != null) {
                callback.onResult(new PinCodeResult(true));
            }
        } catch (SecurityException e) {
            if (callback != null) {
                callback.onResult(new PinCodeResult(e.getError()));
            }
        }
    }

    /**
     * Check if pin code encryption key is exist.
     *
     * @return true if key exist in KeyStore.
     * @throws SecurityException throw exception if something went wrong.
     */
    @Override
    public void isPinCodeEncryptionKeyExist(IPinCodeHelperCallback<Boolean> callback) {
        try {
            final boolean isExist = pfSecurityUtils.isKeystoreContainAlias(PIN_ALIAS);
            if (callback != null) {
                callback.onResult(new PinCodeResult(isExist));
            }
        } catch (SecurityException e) {
            if (callback != null) {
                callback.onResult(new PinCodeResult(e.getError()));
            }
        }
    }

}
