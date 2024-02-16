package com.lockscreen

interface ILockScreenCodeCreateListener {
    fun onCodeCreated(encodedCode: String)
    fun onNewCodeValidationFailed()
}