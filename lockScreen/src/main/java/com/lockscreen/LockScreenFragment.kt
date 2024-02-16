package com.lockscreen

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lockscreen.databinding.FragmentLockScreenBinding

class LockScreenFragment : Fragment(R.layout.fragment_lock_screen) {

    companion object {
        private val TAG = LockScreenFragment::class.java.simpleName
        private const val INSTANCE_STATE_CONFIG = "com.lockscreen.instance_state_config"
        fun create() = LockScreenFragment()
    }

    private var _binding: FragmentLockScreenBinding? = null
    private val binding get() = checkNotNull(_binding)
    private val viewModel: LockScreenViewModel by viewModels()


    private var mUseBiometricAuth = true
    private var mBiometricAuthHardwareDetected = false
    private var mIsCreateMode = false
    private var mCode = ""
    private var mCodeValidation = ""
    private var mEncodedPinCode = ""
    private var topTitle: String = ""
    private var isLeftButtonVisibility: Boolean = false
    private var mConfiguration: LockScreenConfiguration? = null
    private var mCodeCreateListener: ILockScreenCodeCreateListener? = null
    private var mLoginListener: ILockScreenLoginListener? = null
    private var bioAuth: BiometricUIStarter? = null
    private var biometricView: View? = null


    private val mCodeListener = object : CodeView.OnPFCodeListener {
        override fun onCodeCompleted(code: String?) {
            if (mIsCreateMode) {
                mCode = code.orEmpty()
                binding.codeViewConfirm.isVisible = true
                binding.tvPinTitle.text = getString(R.string.repeat_code_title)
                setupPinViewListeners(true)
                setupPinViewConfirmListeners(false)
            } else {
                mCode = code.orEmpty()
                viewModel.checkPin(requireContext(), mEncodedPinCode, mCode)
                    .observe(viewLifecycleOwner) { result ->
                        if (result == null) {
                            return@observe
                        }
                        if (result.error != null) {
                            return@observe
                        }
                        val isCorrect = result.result
                        if (mLoginListener != null) {
                            if (isCorrect) {
                                mLoginListener?.onCodeInputSuccessful()
                            } else {
                                mLoginListener?.onPinLoginFailed()
                                errorAction()
                                cleanCodeViewCode()
                            }
                        }
                        if (!isCorrect && mConfiguration?.isClearCodeOnError == true) {
                            binding.codeView.clearCode()
                        }

                    }
            }


        }

        override fun onCodeNotCompleted(code: String?) {
            if (mIsCreateMode) {
                binding.codeViewConfirm.isVisible = false
                binding.tvPinTitle.text = getString(R.string.create_code_title)
            }


        }
    }

    private val mCodeConfirmListener = object : CodeView.OnPFCodeListener {
        override fun onCodeCompleted(code: String?) {
            mCodeValidation = code.orEmpty()
            if (mCodeValidation.isNotEmpty() && mCode != mCodeValidation) {
                mCodeCreateListener?.onNewCodeValidationFailed()
                cleanCodeViewConfirmCode()
                binding.codeViewConfirm.isVisible = false
                cleanCodeViewCode()
                setupPinViewConfirmListeners(true)
                setupPinViewListeners(false)
                return
            }
            mCodeValidation = ""
            viewModel.encodePin(requireContext(), mCode).observe(viewLifecycleOwner) { result ->
                if (result == null) {
                    return@observe
                }
                if (result.error != null) {
                    Log.d(TAG, "Can not encode pin code")
                    deleteEncodeKey()
                    return@observe
                }
                val encodedCode = result.result
                if (mCodeCreateListener != null) {
                    mCodeCreateListener?.onCodeCreated(encodedCode)
                }

            }
        }

        override fun onCodeNotCompleted(code: String?) {
            with(binding) {
                if (codeViewConfirm.inputCodeLength == 0) {
                    codeViewConfirm.isVisible = false
                    cleanCodeViewCode()
                    setupPinViewConfirmListeners(true)
                    setupPinViewListeners(false)
                }
            }

        }
    }

    private val mOnKeyClickListener = View.OnClickListener {
        if (it is TextView) {
            val text = it.text.toString()
            if (text.length != 1) {
                return@OnClickListener
            }
            binding.codeView.input(text)
        }
    }

    private val mOnKeyConfirmClickListener = View.OnClickListener {
        if (it is TextView) {
            val text = it.text.toString()
            if (text.length != 1) {
                return@OnClickListener
            }
            binding.codeViewConfirm.input(text)
        }
    }

    private val mOnDeleteButtonClickListener = View.OnClickListener {
        binding.codeView.delete()

    }

    private val mOnDeleteButtonConfirmClickListener = View.OnClickListener {
        binding.codeViewConfirm.delete()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLockScreenBinding.inflate(inflater, container, false)
        if (mConfiguration == null) {
            mConfiguration =
                savedInstanceState?.getSerializable(INSTANCE_STATE_CONFIG) as? LockScreenConfiguration
        }
        applyConfiguration(mConfiguration)
        val instantBiometricScan =
            mConfiguration?.isUseBiometric ?: false && mConfiguration?.isAutoShowBiometric ?: false
        val createMode = mConfiguration?.mode == LockScreenConfiguration.MODE_CREATE
        val bioManager = BiometricManager.from(requireContext())
        val callback = createCallback()
        bioAuth = BiometricUIStarter(bioManager, requireContext(), this, callback)
        mBiometricAuthHardwareDetected = bioAuth?.isBiometricAuthAvailable ?: false
        bioAuth?.setTitle(getString(R.string.sign_in_pf))
        if (mBiometricAuthHardwareDetected && instantBiometricScan && !createMode) {
            bioAuth?.setConfirmationRequired(true)
            bioAuth?.startUI()
            val backgroundID = mConfiguration?.biometricBackground ?: -1
            if (backgroundID != -1) { //this will set a new view over the pin fragment which makes it easier to go back from this view
                biometricView = inflater.inflate(backgroundID, container, false)
                (view?.findViewById<View>(R.id.fragment_pf) as ConstraintLayout).addView(
                    biometricView
                )
            }
        }
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(INSTANCE_STATE_CONFIG, mConfiguration)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setupPinViewListeners(isClearListeners = false)
        checkBioSettings()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setConfiguration(configuration: LockScreenConfiguration) {
        this.mConfiguration = configuration
        applyConfiguration(configuration)
    }

    fun setCodeCreateListener(listener: ILockScreenCodeCreateListener) {
        this.mCodeCreateListener = listener
    }

    fun setLoginListener(listener: ILockScreenLoginListener) {
        this.mLoginListener = listener
    }

    fun setEncodedPinCode(encodedPinCode: String) {
        this.mEncodedPinCode = encodedPinCode
    }


    private fun applyConfiguration(configuration: LockScreenConfiguration?) {
        if (configuration == null) {
            return
        }
        topTitle = configuration.topTitle
        isLeftButtonVisibility = configuration.leftButton.isNotEmpty()
        mIsCreateMode = mConfiguration?.mode == LockScreenConfiguration.MODE_CREATE
        mUseBiometricAuth = configuration.isUseBiometric
    }

    private fun setupPinViewListeners(isClearListeners: Boolean) = with(binding) {
        val keyListener = if (isClearListeners) null else mOnKeyClickListener
        val deleteListener = if (isClearListeners) null else mOnDeleteButtonClickListener
        button0.setOnClickListener(keyListener)
        button1.setOnClickListener(keyListener)
        button2.setOnClickListener(keyListener)
        button3.setOnClickListener(keyListener)
        button4.setOnClickListener(keyListener)
        button5.setOnClickListener(keyListener)
        button6.setOnClickListener(keyListener)
        button7.setOnClickListener(keyListener)
        button8.setOnClickListener(keyListener)
        button9.setOnClickListener(keyListener)
        buttonDelete.setOnClickListener(deleteListener)
    }

    private fun setupPinViewConfirmListeners(isClearListeners: Boolean) = with(binding) {
        val keyListener = if (isClearListeners) null else mOnKeyConfirmClickListener
        val deleteListener = if (isClearListeners) null else mOnDeleteButtonConfirmClickListener
        button0.setOnClickListener(keyListener)
        button1.setOnClickListener(keyListener)
        button2.setOnClickListener(keyListener)
        button3.setOnClickListener(keyListener)
        button4.setOnClickListener(keyListener)
        button5.setOnClickListener(keyListener)
        button6.setOnClickListener(keyListener)
        button7.setOnClickListener(keyListener)
        button8.setOnClickListener(keyListener)
        button9.setOnClickListener(keyListener)
        buttonDelete.setOnClickListener(deleteListener)
    }


    private fun initView() = with(binding) {
        codeView.apply {
            setCodeLength(mConfiguration?.codeLength ?: 4)
            setListener(mCodeListener)
        }
        codeViewConfirm.apply {
            setCodeLength(mConfiguration?.codeLength ?: 4)
            setListener(mCodeConfirmListener)
        }

        tvTopTitle.text = topTitle
        val title =
            if (mIsCreateMode) getString(R.string.create_code_title) else getString(R.string.input_code_title)
        binding.tvPinTitle.text = title

        buttonLeft.isVisible = isLeftButtonVisibility
        if (mIsCreateMode) {
            buttonLeft.isVisible = false
            buttonFingerPrint.isVisible = false
        }

    }

    private fun configureRightButton(codeLength: Int) = with(binding) {
        if (mIsCreateMode) {
            buttonDelete.apply {
                isVisible = codeLength > 0
                isEnabled = codeLength > 0
            }
            return@with
        }
    }

    private fun cleanCodeViewCode() {
        mCode = ""
        binding.codeView.clearCode()
    }

    private fun cleanCodeViewConfirmCode() {
        mCode = ""
        binding.codeViewConfirm.clearCode()
    }

    private fun errorAction() {
        if (mConfiguration?.isErrorVibration == true) {
            val v = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            v.vibrate(400L)
        }
        if (mConfiguration?.isErrorAnimation == true) {
            val animShake = AnimationUtils.loadAnimation(requireContext(), R.anim.shake_pf)
            binding.codeView.startAnimation(animShake)
        }
    }

    private fun createCallback(): BiometricPrompt.AuthenticationCallback {
        return object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode == 13 || errorCode == 10) { //13 for "use pin" button and 10 for clicking above the biometricPrompt
                    if (biometricView != null) {
                        (biometricView?.parent as ViewManager).removeView(biometricView)
                        biometricView = null
                    }
                    return
                }
                if (mLoginListener != null) {
                    mLoginListener!!.onBiometricAuthLoginFailed()
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                if (mLoginListener != null) {
                    mLoginListener!!.onBiometricAuthLoginFailed()
                }
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                if (mLoginListener != null) {
                    mLoginListener!!.onBiometricAuthSuccessful()
                }
            }
        }
    }

    private fun checkBioSettings() {
        if (bioAuth?.isBiometricAuthAvailable == true) {
            bioAuth?.apply {
                setConfirmationRequired(false)
                startUI()
            }
            return
        }
    }

    private fun showNoBiometricAuthDialog() {
        AlertDialog.Builder(context).setTitle(R.string.no_fingerprints_title_pf)
            .setMessage(R.string.no_fingerprints_message_pf).setCancelable(false)
            .setNegativeButton(R.string.cancel_pf, null).setPositiveButton(
                R.string.settings_pf
            ) { _, _ ->
                startActivity(
                    Intent(Settings.ACTION_SECURITY_SETTINGS)
                )
            }.create().show()
    }

    private fun deleteEncodeKey() {
        viewModel.delete().observe(viewLifecycleOwner) { result ->
            if (result == null) {
                return@observe
            }
            if (result.error != null) {
                Log.d(TAG, "Can not delete the alias")
                return@observe
            }

        }
    }

}