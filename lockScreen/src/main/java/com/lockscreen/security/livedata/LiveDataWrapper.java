package com.lockscreen.security.livedata;

import androidx.lifecycle.LiveData;

public class LiveDataWrapper<T> extends LiveData<T> {

    public void setData(T data) {
        setValue(data);
    }

}
