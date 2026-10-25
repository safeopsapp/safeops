package com.safeops.app

import android.app.Application
import com.safeops.app.core.logging.SafeOpsLogger
import com.safeops.app.di.AppContainer
import com.safeops.app.di.DefaultAppContainer

class SafeOpsApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            SafeOpsLogger.e("SafeOpsApp", "Uncaught exception on thread: ${thread.name}", throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }
        container = DefaultAppContainer(this)
    }
}
