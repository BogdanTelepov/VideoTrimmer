package com.videotrimmer

import android.app.Application
import kg.dev.videoeditor.utils.BaseUtils

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        BaseUtils.init(this)
    }
}