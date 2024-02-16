package com.lockscreen;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import android.content.Context;

import androidx.annotation.IntDef;

import java.io.Serializable;
import java.lang.annotation.Retention;

public class LockScreenConfiguration implements Serializable {

    private String mLeftButton = "";
    private boolean mUseBiometric = false;
    private boolean mAutoShowBiometric = false;
    private int mBiometricBackground = -1;
    private String mTopTitle = "";
    private String mBottomTitle = "";
    private int mMode = MODE_AUTH;
    private int mCodeLength = 4;
    private boolean mClearCodeOnError = false;
    private boolean mErrorVibration = true;
    private boolean mErrorAnimation = true;
    private boolean mNewCodeValidation = false;
    private String mNewCodeValidationTitle = "";

    private LockScreenConfiguration(Builder builder) {
        mLeftButton = builder.mLeftButton;
        mUseBiometric = builder.mUseBiometric;
        mAutoShowBiometric = builder.mAutoShowBiometric;
        mBiometricBackground = builder.mBiometricBackground;
        mTopTitle = builder.mTopTitle;
        mBottomTitle = builder.mBottomTitle;
        mMode = builder.mMode;
        mCodeLength = builder.mCodeLength;
        mClearCodeOnError = builder.mClearCodeOnError;
        mErrorVibration = builder.mErrorVibration;
        mErrorAnimation = builder.mErrorAnimation;
        mNewCodeValidation = builder.mNewCodeValidation;
        mNewCodeValidationTitle = builder.mNewCodeValidationTitle;
    }

    public String getLeftButton() {
        return mLeftButton;
    }

    public boolean isUseBiometric() {
        return mUseBiometric;
    }

    public boolean isAutoShowBiometric() {
        return mAutoShowBiometric;
    }

    public int getBiometricBackground() {
        return mBiometricBackground;
    }

    public String getTopTitle() {
        return mTopTitle;
    }

    public String getBottomTitle() {
        return mBottomTitle;
    }


    public int getCodeLength() {
        return mCodeLength;
    }

    public boolean isClearCodeOnError() {
        return mClearCodeOnError;
    }

    public boolean isErrorVibration() {
        return mErrorVibration;
    }

    public boolean isErrorAnimation() {
        return mErrorAnimation;
    }

    public boolean isNewCodeValidation() {
        return mNewCodeValidation;
    }

    public String getNewCodeValidationTitle() {
        return mNewCodeValidationTitle;
    }

    @PFLockScreenMode
    public int getMode() {
        return this.mMode;
    }

    public static class Builder {

        private String mLeftButton = "";
        private boolean mUseBiometric = false;
        private boolean mAutoShowBiometric = false;
        private int mBiometricBackground = -1;
        private String mTopTitle = "";

        private String mBottomTitle = "";
        private int mMode = 0;
        private int mCodeLength = 4;
        private boolean mClearCodeOnError = false;
        private boolean mErrorVibration = true;
        private boolean mErrorAnimation = true;
        private boolean mNewCodeValidation = false;
        private String mNewCodeValidationTitle = "";


        public Builder(Context context) {
            mTopTitle = context.getResources().getString(R.string.lock_screen_title_pf);
        }

        public Builder setTopTitle(String title) {
            mTopTitle = title;
            return this;
        }

        public Builder setBottomTitle(String title) {
            mBottomTitle = title;
            return this;
        }

        public Builder setLeftButton(String leftButton) {
            mLeftButton = leftButton;
            return this;
        }

        public Builder setUseBiometric(boolean useBiometric) {
            mUseBiometric = useBiometric;
            return this;
        }

        public Builder setAutoShowBiometric(boolean autoShowBiometric) {
            mAutoShowBiometric = autoShowBiometric;
            return this;
        }

        public Builder setBiometricBackground(int biometricBackground) {
            mBiometricBackground = biometricBackground;
            return this;
        }

        public Builder setMode(@PFLockScreenMode int mode) {
            mMode = mode;
            return this;
        }

        public Builder setCodeLength(int codeLength) {
            this.mCodeLength = codeLength;
            return this;
        }

        public Builder setErrorVibration(boolean errorVibration) {
            mErrorVibration = errorVibration;
            return this;
        }

        public Builder setErrorAnimation(boolean errorAnimation) {
            mErrorAnimation = errorAnimation;
            return this;
        }


        public LockScreenConfiguration build() {
            return new LockScreenConfiguration(this);
        }


    }

    @Retention(SOURCE)
    @IntDef({MODE_CREATE, MODE_AUTH})
    public @interface PFLockScreenMode {
    }

    public static final int MODE_CREATE = 0;
    public static final int MODE_AUTH = 1;

}
