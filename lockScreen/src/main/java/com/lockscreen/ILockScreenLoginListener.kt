package com.lockscreen

interface ILockScreenLoginListener {
    fun onCodeInputSuccessful()
    fun onBiometricAuthSuccessful()
    fun onPinLoginFailed()
    fun onBiometricAuthLoginFailed()
}