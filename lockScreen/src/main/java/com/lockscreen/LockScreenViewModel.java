package com.lockscreen;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.lockscreen.security.PinCodeResult;
import com.lockscreen.security.SecurityManager;
import com.lockscreen.security.livedata.LiveDataWrapper;


public class LockScreenViewModel extends ViewModel {

    public LiveData<PinCodeResult<String>> encodePin(Context context, String pin) {
        final LiveDataWrapper<PinCodeResult<String>> liveData = new LiveDataWrapper<>();
        SecurityManager.getInstance().getPinCodeHelper().encodePin(
                context,
                pin,
                liveData::setData
        );
        return liveData;
    }

    public LiveData<PinCodeResult<Boolean>> checkPin(Context context, String encodedPin, String pin) {
        final LiveDataWrapper<PinCodeResult<Boolean>> liveData = new LiveDataWrapper<>();
        SecurityManager.getInstance().getPinCodeHelper().checkPin(
                context,
                encodedPin,
                pin,
                liveData::setData
        );
        return liveData;
    }

    public LiveData<PinCodeResult<Boolean>> delete() {
        final LiveDataWrapper<PinCodeResult<Boolean>> liveData = new LiveDataWrapper<>();
        SecurityManager.getInstance().getPinCodeHelper().delete(
                liveData::setData
        );
        return liveData;
    }

    public LiveData<PinCodeResult<Boolean>> isPinCodeEncryptionKeyExist() {
        final LiveDataWrapper<PinCodeResult<Boolean>> liveData = new LiveDataWrapper<>();
        SecurityManager.getInstance().getPinCodeHelper().isPinCodeEncryptionKeyExist(
                liveData::setData
        );
        return liveData;
    }

}
