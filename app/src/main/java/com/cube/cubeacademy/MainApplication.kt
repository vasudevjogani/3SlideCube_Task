package com.cube.cubeacademy

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {

    companion object {
        lateinit var application: MainApplication
            private set //we want only MainApplication can set the instance value
    }

    override fun onCreate() {
        super.onCreate()
        application = this
    }
}