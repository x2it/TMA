package com.realtor.geeksales

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GeekSalesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            android.util.Log.e("TMA_Crash", "Uncaught exception on $thread", throwable)
        }
    }
}