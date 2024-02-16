package com.videotrimmer

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lockscreen.LockScreenFragment
import com.lockscreen.ILockScreenCodeCreateListener
import com.lockscreen.ILockScreenLoginListener
import com.lockscreen.LockScreenConfiguration
import com.lockscreen.LockScreenConfiguration.MODE_AUTH
import com.lockscreen.LockScreenConfiguration.MODE_CREATE
import com.lockscreen.LockScreenViewModel

class MainActivity : AppCompatActivity(R.layout.activity_main) {


    private val mCodeCreateListener = object : ILockScreenCodeCreateListener {
        override fun onCodeCreated(encodedCode: String) {
            Toast.makeText(this@MainActivity, "Code Created $encodedCode", Toast.LENGTH_LONG).show()
            PreferencesSettings.saveToPref(this@MainActivity, encodedCode)
        }

        override fun onNewCodeValidationFailed() {
            Toast.makeText(this@MainActivity, "Code validation error", Toast.LENGTH_SHORT).show()
        }
    }

    private val mLoginListener = object : ILockScreenLoginListener {
        override fun onCodeInputSuccessful() {
            Toast.makeText(this@MainActivity, "Code successful", Toast.LENGTH_SHORT).show()
        }

        override fun onBiometricAuthSuccessful() {
            Toast.makeText(
                this@MainActivity, "Biometric authentication successful", Toast.LENGTH_SHORT
            ).show()
        }

        override fun onPinLoginFailed() {
            Toast.makeText(this@MainActivity, "Pin failed", Toast.LENGTH_SHORT).show()
        }

        override fun onBiometricAuthLoginFailed() {
            Toast.makeText(this@MainActivity, "Biometric authentication failed", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showLockScreenFragment()

    }

    private fun showLockScreenFragment() {
        LockScreenViewModel().isPinCodeEncryptionKeyExist.observe(this) { result ->
            if (result == null) {
                return@observe
            }
            if (result.error != null) {
                Toast.makeText(this@MainActivity, "Can not get pin code info", Toast.LENGTH_SHORT)
                    .show()
                return@observe
            }
            showLockScreenFragment(result.result)
        }
    }

    private fun showLockScreenFragment(isPinExist: Boolean) {
        val mode = if (isPinExist) MODE_AUTH else MODE_CREATE
        val builder = LockScreenConfiguration.Builder(this)
            .setCodeLength(4)
            .setAutoShowBiometric(true)
            .setUseBiometric(true)
            .setMode(mode)
            .build()
        val fragment = LockScreenFragment.create()
        fragment.setConfiguration(builder)
        if (isPinExist) {
            fragment.setEncodedPinCode(PreferencesSettings.getCode(this))
            fragment.setLoginListener(mLoginListener)
        }

        fragment.setCodeCreateListener(mCodeCreateListener)
        replace(fragment, addToBackStack = false)
    }

}