package com.lockscreen.security;

public class SecurityException extends Exception {

    private final Integer mCode;

    /**
     * Constructor.
     *
     * @param message exception message.
     * @param code    error code.
     */
    public SecurityException(String message, Integer code) {
        super(message);
        mCode = code;
    }

    /**
     * Get error code.
     *
     * @return error code.
     */
    public Integer getCode() {
        return mCode;
    }

    /**
     * Get PFSecurityError object.
     *
     * @return PFSecurityError from PFSecurityException message and error code.
     */
    public SecurityError getError() {
        return new SecurityError(getMessage(), getCode());
    }
}
